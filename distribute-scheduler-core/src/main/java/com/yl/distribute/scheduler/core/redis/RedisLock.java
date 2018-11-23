package com.yl.distribute.scheduler.core.redis;

import java.util.List;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class RedisLock {
	
	private static Log LOG = LogFactory.getLog(RedisLock.class);
    
    public static String acquireLockWithTimeout(
            Jedis jedis, String lockName, long acquireTimeout, long lockTimeout)
        {
            String identifier = UUID.randomUUID().toString();   //锁的值
            String lockKey = "lock:" + lockName;     //锁的键
            int lockExpire = (int)(lockTimeout / 1000);     //锁的过期时间

            long end = System.currentTimeMillis() + acquireTimeout;     //尝试获取锁的时限
            while (System.currentTimeMillis() < end) {      //判断是否超过获取锁的时限
                if (jedis.setnx(lockKey, identifier) == 1){  //判断设置锁的值是否成功
                    jedis.expire(lockKey, lockExpire);   //设置锁的过期时间
                    return identifier;          //返回锁的值
                }
                if (jedis.ttl(lockKey) == -1) {      //判断锁是否超时
                    jedis.expire(lockKey, lockExpire);
                }

                try {
                    Thread.sleep(100);    //等待1秒后重新尝试设置锁的值
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    LOG.error(e);
                }
            }
            // 获取锁失败时返回null
            return null;
        }

    // 无论是否加锁成功，必须调用
    public static boolean releaseLock(Jedis jedis, String lockName, String identifier) {
        String lockKey = "lock:" + lockName;    //锁的键

        while (true){
            jedis.watch(lockKey);    //监视锁的键
            if (identifier.equals(jedis.get(lockKey))){  //判断锁的值是否和加锁时设置的一致，即检查进程是否仍然持有锁
                Transaction trans = jedis.multi();
                trans.del(lockKey);             //在Redis事务中释放锁
                List<Object> results = trans.exec();
                if (results == null){   
                    continue;       //事务执行失败后重试（监视的键被修改导致事务失败，重新监视并释放锁）
                }
                return true;
            }

            jedis.unwatch();     //解除监视
            break;
        }
        return false;
    }
}
