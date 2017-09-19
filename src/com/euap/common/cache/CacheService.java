package com.euap.common.cache;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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



    public void evict(String cacheKey, String hashKey) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        hashOperators.delete(cacheKey, hashKey);
    }


    public void clear(String cacheKey) {
        HashOperations hashOperators = stringRedisTemplate.opsForHash();
        hashOperators.getOperations().delete(cacheKey);
    }


}
