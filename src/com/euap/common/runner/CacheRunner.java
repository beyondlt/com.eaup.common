package com.euap.common.runner;

import com.euap.common.aop.AopTargetUtils;
import com.euap.common.cache.Cache;
import com.euap.common.cache.RefreshCache;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/19 0019.
 */
@Component
@Order(value = 1)
public class CacheRunner implements CommandLineRunner{

    private static final Logger logger = LogManager.getLogger(CacheRunner.class);

    @Autowired
    private ApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        Map<String,Object> beans=context.getBeansWithAnnotation(Cache.class);
        for(Object object:beans.values()){
            Object bean= AopTargetUtils.getTarget(object);
            String name=((Cache)bean.getClass().getAnnotation(Cache.class)).name();
            logger.info("发现缓存：["+name+"]   位置："+bean.getClass().getName());
            long start=0;
          for(Method method: bean.getClass().getDeclaredMethods()){
              if(method.isAnnotationPresent(RefreshCache.class)){
                 String desc=((RefreshCache)method.getAnnotation(RefreshCache.class)).desc();
                  logger.info("开始："+desc);
                  start=System.currentTimeMillis();
                  method.invoke(bean,null);
                  logger.info(desc+"结束,耗时："+(System.currentTimeMillis()-start)+"毫秒");
              }

          }
        }

    }
}
