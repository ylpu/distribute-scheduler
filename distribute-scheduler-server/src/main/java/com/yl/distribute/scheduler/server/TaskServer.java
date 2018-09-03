package com.yl.distribute.scheduler.server;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.enums.TaskStatus;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import com.yl.distribute.scheduler.core.config.Configuration;
import com.yl.distribute.scheduler.core.jersey.JerseyClient;
import com.yl.distribute.scheduler.core.resource.rpc.ResourceProxy;
import com.yl.distribute.scheduler.core.resource.service.ResourceService;
import com.yl.distribute.scheduler.core.zk.ZKHelper;
import com.yl.distribute.scheduler.server.handler.TaskServerHandler;
import com.yl.distribute.scheduler.server.processor.TaskCall;
import com.yl.distribute.scheduler.server.processor.TaskProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
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
    
    private int zkPort;
    
    public TaskServer(int zkPort) {
        this.zkPort = zkPort;
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
            ChannelFuture future = b.bind(zkPort).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    /**
     * 注册server的信息到zk上
     * @param zkServers
     * @param path
     */
    public void regServer(String zkServers, String path) {  
        ZkClient client = ZKHelper.getClient(zkServers);          
        if(client.exists(path)) {     
            ZKHelper.delete(client, path);     
        }
        HostInfo hostInfo = new HostInfo();
        setRegistData(hostInfo);
        client.createEphemeral(path, hostInfo); 
    } 
    
    
    private void setRegistData(HostInfo hostInfo) {
        hostInfo.setTotalCores(MetricsUtils.getAvailiableProcessors());
        hostInfo.setTotalMemory(MetricsUtils.getMemInfo());
        hostInfo.setAvailableCores(MetricsUtils.getAvailiableProcessors());
        hostInfo.setAvailableMemory(MetricsUtils.getMemInfo());
        hostInfo.setIp(MetricsUtils.getHostIpAddress() + ":" + zkPort);
        hostInfo.setHostName(MetricsUtils.getHostName() + "-" + zkPort);
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
     * 系统异常结束释放资源并更新任务状态
     * @throws Exception
     */
    public void addShutDownHook() throws Exception {
        Runtime.getRuntime().addShutdownHook(
        new Thread(new Runnable() {
            public void run(){   
                TaskCall call = null;
                LinkedBlockingQueue<TaskCall> queue = TaskProcessor.getQueue();
                while ((call = queue.poll()) != null) {            
                    LOG.info("start to release resource for " + call.getTask().getRunningServer());
                    releaseResource(call);
                    updateTask(call.getTask());
             }
          }
      }));
    }
    
    private void releaseResource(TaskCall call) {
        LOG.info("start to release resource for " + call.getTask().getRunningServer());
        ResourceService service = ResourceProxy.get(ResourceService.class);
        service.addResource(call.getTask().getRunningServer(), call.getTask().getJob());
        service.decreaseTask(call.getTask().getRunningServer());
    }
    /**
     * server异常终止更新任务为失败
     * @param taskRequest
     */
    private void updateTask(TaskRequest taskRequest) {
        Properties prop = Configuration.getConfig("config.properties");        
        String taskApi = Configuration.getString(prop, "task.web.api");
        long elapseTime = (System.currentTimeMillis() - taskRequest.getStartTime().getTime())/1000;
        taskRequest.setTaskStatus(TaskStatus.FAILED);
        taskRequest.setEndTime(new Date());
        taskRequest.setElapseTime(elapseTime);
        JerseyClient.update(taskApi + "/" + "updateTask", taskRequest);
    }
    
    public static void start(Map<String,Object> parameterMap) throws Exception{
        String path = parameterMap.get("poolPath") + MetricsUtils.getHostName() + "-" + parameterMap.get("zkPort").toString(); 
        TaskServer server = new TaskServer(NumberUtils.toInt(parameterMap.get("zkPort").toString()));
        server.regServer(parameterMap.get("zkServers").toString(),path);
        server.startJettyServer(NumberUtils.toInt(parameterMap.get("jettyPort").toString()));
        server.addShutDownHook();
        server.start();
    }
    
    public static void main(String[] args) throws Exception {
        Properties prop = Configuration.getConfig("config.properties");        
        int zkPort = Configuration.getInt(prop, "zk.regist.default.port");
        int jettyPort = Configuration.getInt(prop, "jetty.server.port");
        String poolPath = Configuration.getString(prop, "zk.regist.default.pool.path");
        
        if (args.length > 1) {           
           zkPort = NumberUtils.toInt(args[0],zkPort);   
           poolPath = args[1];             
        }
        
        String zkServers = Configuration.getString(prop, "zk.server.list");
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("zkPort", zkPort);
        parameterMap.put("jettyPort", jettyPort);
        parameterMap.put("poolPath", poolPath);
        parameterMap.put("zkServers", zkServers);
        start(parameterMap);   
    }
}