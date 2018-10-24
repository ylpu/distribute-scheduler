package com.yl.distribute.scheduler.server;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.bean.TaskResponse;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.redis.RedisClient;
import com.yl.distribute.scheduler.core.task.TaskManager;
import com.yl.distribute.scheduler.core.zk.ZKHelper;
import com.yl.distribute.scheduler.server.handler.TaskCall;
import com.yl.distribute.scheduler.server.handler.TaskServerHandler;
import com.yl.distribute.scheduler.server.handler.TaskTracker;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

public class TaskServer {
    
    private static Log LOG = LogFactory.getLog(TaskServer.class);
    
    private static final String REDIS_CONFIG = "redis.properties";
    
    private int serverPort;
    
    public TaskServer(int serverPort) {
        this.serverPort = serverPort;
    }
    
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()        
                            .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                            .addLast(new LengthFieldPrepender(4))
                            //使用netty自己的encoder和decoder,根据需要可以使用core中的kryo或protobuf
                            .addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())))
                            .addLast(new ObjectEncoder())
                            .addLast(new DefaultEventExecutorGroup(8),new TaskServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(serverPort).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    /**
     * 注册server路径信息到zk上
     * 注册server数据信息到redis
     * @param zkServers
     * @param path
     */
    public void regServer(ZkClient client, Map<String,Object> parameterMap) {  
    	String poolPath = parameterMap.get("serverPoolPath").toString();
        if(!client.exists(poolPath)) {     
            ZKHelper.createNode(client, poolPath,null);     
        }
        String serverPath = poolPath + "/" + MetricsUtils.getHostName() + ":" + parameterMap.get("serverPort").toString();
        if(client.exists(serverPath)) {     
            ZKHelper.delete(client, serverPath);     
        }
        HostInfo hostInfo = new HostInfo();
        setRegistData(hostInfo);
        ZKHelper.createEphemeralNode(client,serverPath, hostInfo);
        RedisClient redisClient = null;
        try {
            redisClient = RedisClient.getInstance(Configuration.getConfig(REDIS_CONFIG));        
            redisClient.setObject(MetricsUtils.getHostName() + ":" + serverPort, hostInfo, 0);
        }finally {
        	redisClient.close();
        }
    } 
    
    
    private void setRegistData(HostInfo hostInfo) {
        hostInfo.setTotalCores(MetricsUtils.getAvailiableProcessors());
        hostInfo.setTotalMemory(MetricsUtils.getMemInfo());
        hostInfo.setAvailableCores(MetricsUtils.getAvailiableProcessors());
        hostInfo.setAvailableMemory(MetricsUtils.getMemInfo());
        hostInfo.setIp(MetricsUtils.getHostIpAddress() + ":" + serverPort);
        hostInfo.setHostName(MetricsUtils.getHostName() + ":" + serverPort);
    }
    
    /**
     * 启动jetty服务器供客户端获取job的log信息
     * @param port
     * @throws Exception
     */
    public void startJettyServer(int port) throws Exception {
        Server server = new Server(port);
        XmlConfiguration config = new XmlConfiguration(new FileInputStream("./WebContent/WEB-INF/jetty.xml"));  
        config.configure(server);
        WebAppContext webAppContext = new WebAppContext("WebContent","/");
        webAppContext.setDescriptor("./WebContent/WEB-INF/web.xml");
        webAppContext.setResourceBase("./WebContent/jobfiles/");
        webAppContext.setDisplayName("jetty");
        webAppContext.setContextPath("/server");
        webAppContext.setConfigurationDiscovered(true);
        webAppContext.setParentLoaderPriority(true);
        server.setHandler(webAppContext);
        try{
            server.start();
        }catch(Exception e){
            LOG.error(e);
        }
    }  
    
    /**
     * 系统异常结束更新任务状态为失败
     * @throws Exception
     */
    public void addShutDownHook() throws Exception {
        Runtime.getRuntime().addShutdownHook(
        new Thread(new Runnable() {
            public void run(){   
                Map<TaskRequest,ChannelHandlerContext> taskMap = TaskTracker.getTaskMap();
                if(taskMap != null && taskMap.size() > 0) {
                    for(Entry<TaskRequest,ChannelHandlerContext> entry : taskMap.entrySet()) {                        
                        writeResponse(new TaskCall(entry.getValue(),entry.getKey()));
                        TaskManager.getInstance().updateTask(entry.getKey(),TaskStatus.FAILED);
                    }
                }       
          }
      }));
    }
    
    private void writeResponse(TaskCall call) {
        TaskResponse response = new TaskResponse();
        response.setId(call.getTaskRequest().getId());
        response.setTaskId(call.getTaskRequest().getTaskId());   
        response.setFailedTimes(call.getTaskRequest().getFailedTimes());
        response.setJobId(call.getTaskRequest().getJob().getJobId());
        response.setTaskStatus(TaskStatus.FAILED);                  
        call.getCtx().writeAndFlush(response);
    }  
    
    public static void start(Map<String,Object> parameterMap) throws Exception{
        TaskServer server = new TaskServer(NumberUtils.toInt(parameterMap.get("serverPort").toString()));
        
        ZkClient client = ZKHelper.getClient(parameterMap.get("zkServers").toString());
//        String path = parameterMap.get("serverPoolPath") + MetricsUtils.getHostName() + ":" + parameterMap.get("serverPort").toString();
        server.regServer(client,parameterMap);
        server.startJettyServer(NumberUtils.toInt(parameterMap.get("jettyPort").toString()));
        server.addShutDownHook();
        server.start();
    }
    
    public static void main(String[] args) throws Exception {
        Properties prop = Configuration.getConfig("Config.properties");        
        int serverPort = Configuration.getInt(prop, "server.regist.default.port");
        String serverPoolPath = Configuration.getString(prop, "server.regist.default.pool.path");
        int jettyPort = Configuration.getInt(prop, "jetty.server.port");       
        
        if (args.length > 1) {           
            serverPort = NumberUtils.toInt(args[0],serverPort);   
            serverPoolPath = args[1];             
        }
        
        String zkServers = Configuration.getString(prop, "zk.server.list");
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("serverPort", serverPort);
        parameterMap.put("serverPoolPath", serverPoolPath);
        parameterMap.put("jettyPort", jettyPort);        
        parameterMap.put("zkServers", zkServers);
        start(parameterMap);   
    }
}