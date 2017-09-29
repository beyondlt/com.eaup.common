package com.euap.common.utils;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by Administrator on 2017/9/26 0026.
 */
public class JacksonUtil {

    private final static ObjectMapper objectMapper=new ObjectMapper();

    public static ObjectMapper getObjectMapper(){
        return objectMapper;
    }
}
