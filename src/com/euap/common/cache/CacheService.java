package com.euap.common.cache;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/9/19 0019.
 */

@Service
public class CacheService {

    private static final Logger logger = LogManager.getLogger(CacheService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();


    public Object get(String cacheKey, String hashKey, Class hashValueClass) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        if (hashOperators.hasKey(cacheKey, hashKey)) {
            String context = (String) hashOperators.get(cacheKey, hashKey);
            try {
                return objectMapper.readValue(context, hashValueClass);
            } catch (IOException e) {
                logger.error(ExceptionUtils.getMessage(e));
            }
        }
        return null;
    }

    public List multiGet(String cacheKey, String[] hashKeys, Class hashValueClass) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        List<String> keys = new ArrayList<>();
        Collections.addAll(keys, hashKeys);
        List<String> values = hashOperators.multiGet(cacheKey, keys);
        List list = null;
        try {
            list = new ArrayList<>();
            for (String obj : values) {
                list.add(objectMapper.readValue(obj, hashValueClass));
            }
        } catch (IOException e) {
            logger.error(ExceptionUtils.getMessage(e));
        }

        return list;
    }

    public List getAll(String cacheKey,  Class hashValueClass) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        List<String> values = hashOperators.values(cacheKey);
        List list = null;
        try {
            list = new ArrayList<>();
            for (String obj : values) {
                list.add(objectMapper.readValue(obj, hashValueClass));
            }
        } catch (IOException e) {
            logger.error(ExceptionUtils.getMessage(e));
        }

        return list;
    }

    public boolean hasKey(String cacheKey, String hashKey) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        return hashOperators.hasKey(cacheKey, hashKey);

    }

    public void put(String cacheKey, String hashKey, Object hashValue) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        String value = null;
        try {
            value = objectMapper.writeValueAsString(hashValue);
            hashOperators.put(cacheKey, hashKey, value);
        } catch (IOException e) {
            logger.error(ExceptionUtils.getMessage(e));
        }
    }

    public HashOperations getOperations() {
        return stringRedisTemplate.opsForHash();
    }

    public void batchPut(String cacheKey, final List objects, ICachePipeLinedHandler handler) {
        final StringRedisSerializer stringRedisSerializer = (StringRedisSerializer) stringRedisTemplate.getKeySerializer();
        stringRedisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for (Object object : objects) {
                    String hashkey = handler.getHashkey(object);
                    String hashObject = "";
                    try {
                        hashObject = objectMapper.writeValueAsString(object);
                    } catch (IOException e) {
                        logger.error(ExceptionUtils.getMessage(e));
                    }
                    connection.hSet(stringRedisSerializer.serialize(cacheKey), stringRedisSerializer.serialize(hashkey), stringRedisSerializer.serialize(hashObject));
                }
                return null;
            }
        });
    }


    public void evict(String cacheKey, String... hashKey) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        hashOperators.delete(cacheKey, hashKey);
    }


    public void clear(String cacheKey) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        hashOperators.getOperations().delete(cacheKey);
    }

    public static void main(String[]args){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName("127.0.0.1");
        factory.setPort(6379);
        factory.setPassword("");
        factory.setPoolConfig(poolConfig);
        factory.afterPropertiesSet();
        StringRedisTemplate template = new StringRedisTemplate();

        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        HashOperations hashOperations=template.opsForHash();
//        hashOperations.put("test_cache","001","001");
//        Set<String> set = hashOperations.keys(Const.INST);
//        for(String key:set){
//            System.out.println(key);
//        }
        List<String> strList=hashOperations.values(Const.INST);
        for(String str:strList){
            System.out.println(str);
        }
    }


}
