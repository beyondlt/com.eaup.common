package com.euap.common.runtime;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public class RequestThreadLocal {

    private static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<HttpServletRequest>();

    public static void set(HttpServletRequest request){
        requestThreadLocal.set(request);
    }
    public static HttpServletRequest get(){
        return requestThreadLocal.get();
    }

    public static String getWebappUrl(){
        return "http://localhost:8085/euap";
    }

    public static String getWsappUrl(){
        return "ws://localhost:8085/euap";
    }

}