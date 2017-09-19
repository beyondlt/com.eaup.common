package com.euap.common.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/19 0019.
 */
@Component
public class HashRedisTemplate {
    @Autowired
    private static JedisConnectionFactory jedisConnectionFactory;

    private static StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    private static Map<String, RedisTemplate> redisTemplateManager = new HashMap<String, RedisTemplate>();


    private HashRedisTemplate() {

    }

    public static RedisTemplate getSpringInstance(Class T) {
        return getInstance(jedisConnectionFactory,T);
    }

    public static RedisTemplate getInstance(JedisConnectionFactory jedisConnectionFactory, Class T) {
        String templateKey = T.getName() + jedisConnectionFactory.hashCode();
        if (redisTemplateManager.containsKey(templateKey)) {
            return redisTemplateManager.get(templateKey);
        } else {
            RedisTemplate redisTemplate = new RedisTemplate();
            redisTemplate.setConnectionFactory(jedisConnectionFactory);            ;
            redisTemplate.setKeySerializer(stringRedisSerializer);
            redisTemplate.setHashKeySerializer(stringRedisSerializer);
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(T);
            redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
            redisTemplateManager.put(templateKey, redisTemplate);
            return redisTemplate;
        }
    }


}
