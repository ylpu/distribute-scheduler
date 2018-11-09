package com.yl.distribute.scheduler.core.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import com.yl.distribute.scheduler.common.bean.HostInfo;
import com.yl.distribute.scheduler.common.bean.JobConf;
import com.yl.distribute.scheduler.common.bean.TaskRequest;
import com.yl.distribute.scheduler.common.utils.MetricsUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 主要用来给用户提供一个设计完备的，通过jedis的jar包来管理redis内存数据库的各种方法
 */
public class RedisClient {    
    
    private static String host = "127.0.0.1";
    private static int port = 6379;    //访问密码
    private static String auth = "123456"; 
    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 50;    
    private static int MIN_IDLE = 8;
    private static int MAX_TOTAL = 200;
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 10000;       
    private static long ACQUIER_LOCK_TIME = 10 * 1000;    
    private static long LOCK_TIME = 1000;    
    private static int timeout = 60000;    
    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;    
    // 设置为0的话就是永远都不会过期
    private static int expire = 0; 
    // 定义一个管理池，所有的redisManager共同使用。
    private static JedisPool jedisPool = null;  
    
    private static RedisClient instance;
    
    private RedisClient(Properties prop) {
        if(prop == null){
            init();
        }else{
            init(prop); 
        }        
    }
    
    public static synchronized RedisClient getInstance(Properties prop) {
        if(instance == null){
            instance = new RedisClient(prop);
        }
        return instance;
    }
 
    /**
     * 
     * 初始化方法,在这个方法中通过host和port来初始化jedispool。
     * 
     */
    public void init() {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMinIdle(MIN_IDLE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxTotal(MAX_TOTAL);            
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            if(StringUtils.isNotBlank(auth)) {
                jedisPool = new JedisPool(config, host, port, timeout, auth);
            } else {
                jedisPool = new JedisPool(config, host, port, timeout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void init(Properties prop) {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            
            config.setMinIdle(prop.getProperty("redis.minidle") == null ?
                    MIN_IDLE : NumberUtils.toInt(prop.getProperty("redis.minidle")));
            config.setMaxIdle(prop.getProperty("redis.maxidle") == null ?
                    MAX_IDLE : NumberUtils.toInt(prop.getProperty("redis.maxidle")));
            config.setMaxTotal(prop.getProperty("redis.maxtotal") == null ?
                    MAX_TOTAL : NumberUtils.toInt(prop.getProperty("redis.maxtotal")));
            config.setMaxWaitMillis(prop.getProperty("redis.maxwait") == null ? 
                    MAX_WAIT : NumberUtils.toLong(prop.getProperty("redis.maxwait")));
            config.setTestOnBorrow(prop.getProperty("redis.testOnBorrow") == null ? 
                    TEST_ON_BORROW : Boolean.valueOf(prop.getProperty("redis.testOnBorrow")));
            
            config.setTestWhileIdle(true);  
            config.setTestOnReturn(true);
            config.setTimeBetweenEvictionRunsMillis(30000);
            //表示idle object evitor每次扫描的最多的对象数
            config.setNumTestsPerEvictionRun(10);
            //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
            config.setMinEvictableIdleTimeMillis(60000);
          
            host = prop.getProperty("redis.host") == null ? 
                    host : prop.getProperty("redis.host");
            port = prop.getProperty("redis.port") == null ? 
                    port : NumberUtils.toInt(prop.getProperty("redis.port"));
            auth = prop.getProperty("redis.auth") == null ? 
                    auth : prop.getProperty("redis.auth");
            
            timeout = prop.getProperty("redis.timeout") == null ? 
                    timeout : NumberUtils.toInt(prop.getProperty("redis.timeout"));
            
            if(StringUtils.isNotBlank(auth)) {
                jedisPool = new JedisPool(config, host, port, timeout, auth);
            } else {
                jedisPool = new JedisPool(config, host, port, timeout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                System.out.println("can not get redis pool");
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * get value from redis
     * 
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        byte[] value = null;
        Jedis jedis = getJedis();
        try {
            value = jedis.get(key);
        } finally {
            jedis.close();
        }
        return value;
    }
 
    /**
     * get value from redis
     * 
     * @param key
     * @return
     */
    public String get(String key) {
        String value = null;
        Jedis jedis = getJedis();
        try {
            value = jedis.get(key);
        } finally {
            jedis.close();
        }
        return value;
    }
 
    /**
     * set
     * 
     * @param key
     * @param value
     * @return
     */
    public void set(byte[] key, byte[] value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        } finally {
            jedis.close();
        }
    }
 
    /**
     * set
     * 
     * @param key
     * @param value
     * @return
     */
    public void set(String key, String value) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        } finally {
            jedis.close();
        }
    }
 
    /**
     * set
     * 
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public void set(byte[] key, byte[] value, int expire) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        } finally {
            jedis.close();
        }
    }
 
    /**
     * set
     * 
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public void set(String key, String value, int expire) {
        Jedis jedis = getJedis();
        try {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        } finally {
            jedis.close();
        }
    }
 
    /**
     * del
     * 
     * @param key
     */
    public void del(byte[] key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }
 
    /**
     * del
     * 
     * @param key
     */
    public void del(String key) {
        Jedis jedis = getJedis();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }
 
    /**
     * 判断指定键是否存在
     * @param key
     * @return
     */
    public boolean exists(String key) {
        Jedis jedis = getJedis();
        boolean flag = jedis.exists(key);
        jedis.close();
        return flag ;
    }
    
    /**
     * 获取key对应的值剩余存活时间
     * @param key
     * @return
     *      正数：剩余的时间(秒)
     *      负数：已过期
     */
    public Long ttlKey(String key) {
        Jedis jedis = getJedis();
        try {
            return jedis.ttl(key);
        } catch(Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            jedis.close();
        }
    }
    
    /**
     * 获取key对应的值剩余存活时间
     * @param key
     * @return
     *      正数：剩余的时间(秒)
     *      负数：已过期
     */
    public Long ttlKey(byte[] key) {
        Jedis jedis = getJedis();
        try {
            return jedis.ttl(key);
        } catch(Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            jedis.close();
        }
    }
    
    /**
     * 存储对象
     * @param key
     * @param obj
     * @param expire
     */
    public <T> void setObject(String key,T obj,int expire) {
        byte[] data = ObjTOSerialize(obj);
        Jedis jedis = getJedis();
        jedis.set(key.getBytes(),data);
        if(expire != 0) {
            jedis.expire(key, expire);
        }
        jedis.close();
    }
    
    /**
     * 获取对象
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getObject(String key) {
        Jedis jedis = getJedis();
        byte[] data = jedis.get(key.getBytes());
        Object obj = null;
        if(data != null) {
            obj = unSerialize(data);
        }
        jedis.close();
        return (T)obj;
    }
    
    public void getAndInc(TaskRequest taskRequest){
        
        Jedis jedis = getJedis();        
        String identifier = RedisLock.acquireLockWithTimeout(
                jedis,taskRequest.getRunningHost(),ACQUIER_LOCK_TIME,LOCK_TIME);
        HostInfo hostInfo = getObject(taskRequest.getRunningHost());
        hostInfo.setAvailableMemory(hostInfo.getAvailableMemory() + MetricsUtils.getTaskMemory(taskRequest.getJob()));
        hostInfo.setAvailableCores(hostInfo.getAvailableCores() + 1);
        setObject(taskRequest.getRunningHost(), hostInfo,0);   
        RedisLock.releaseLock(jedis, taskRequest.getRunningHost(), identifier);
        jedis.close();
    }
    
    public void getAndSub(TaskRequest taskRequest){
        
        Jedis jedis = getJedis();        
        String identifier = RedisLock.acquireLockWithTimeout(
                jedis,taskRequest.getRunningHost(),ACQUIER_LOCK_TIME,LOCK_TIME);
        HostInfo hostInfo = getObject(taskRequest.getRunningHost());
        hostInfo.setAvailableMemory(hostInfo.getAvailableMemory() - MetricsUtils.getTaskMemory(taskRequest.getJob()));
        hostInfo.setAvailableCores(hostInfo.getAvailableCores() - 1);
        setObject(taskRequest.getRunningHost(), hostInfo,0);        
        RedisLock.releaseLock(jedis, taskRequest.getRunningHost(), identifier);
        jedis.close();
    }
    
    /**
     * 
     * 序列化一个对象
     * @param obj
     * @return
     */
    public byte[] ObjTOSerialize(Object obj) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream byteOut = null;
        try {
            byteOut = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteOut);
            oos.writeObject(obj);
            byte[] bytes = byteOut.toByteArray();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
    /**
     * 反序列化一个对象
     * @param bytes
     * @return
     */
    public Object unSerialize(byte[] bytes) {
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(bytes);
            ObjectInputStream objIn = new ObjectInputStream(in);
            return objIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void close(){
        if(jedisPool != null){
            jedisPool.close();
        }
    }
    
    public static void main(String[] args){
    	
        HostInfo redisHostInfo = RedisClient.getInstance(null).getObject("ppd-02020301:8081");
        System.out.println(redisHostInfo.getAvailableMemory());
       
        for(int i =0; i< 100; i++){       	
            TaskRequest tr = new TaskRequest();
            JobConf jc = new JobConf();            
            jc.setCommand("java -jar abc.jar");
            jc.setResourceParameters("-memory100m -cpu4");
            tr.setJob(jc);
            tr.setRunningHost("ppd-02020301:8081");
            
            Thread t = new Thread(new Runnable(){
            	@Override
                public void run() {
                    RedisClient.getInstance(null).getAndInc(tr);
                }
                
            });
            t.start();            
        }
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      HostInfo redisHostInfo1 = RedisClient.getInstance(null).getObject("ppd-02020301:8081");
      System.out.println(redisHostInfo1.getAvailableMemory());
//      RedisClient.getInstance(null).del("asus-PC:8081".getBytes());
    } 
}