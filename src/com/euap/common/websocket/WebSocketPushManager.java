package com.euap.common.websocket;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2017/9/21 0021.
 */
@Component
@ServerEndpoint(value = "/push/{key}", configurator = GetHttpSessionConfigurator.class)

public class WebSocketPushManager {

    private final static Logger logger = LogManager.getLogger(WebSocketPushManager.class);

    private final static List<String> messages= Collections.synchronizedList(new ArrayList<String>());
    // Map<visitorId,Map<key,Set<Session>>>
    private final static Map<String,Map<String,Set<Session>>> visitorToSession=Collections.synchronizedMap(new HashMap<>());
   // Map<key,Map<Session,visitorId>>
    private final static Map<String,Map<Session,String>> keyToVisitor=Collections.synchronizedMap(new HashMap<>());

    private final static  String MESSAGE_CONCAT_MESSAGE="#";




    private  void regeisterSession(String key,Session session, EndpointConfig config){
        HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String visitorId=String.valueOf(httpSession.getAttribute("userId"));
        if( visitorToSession.containsKey(visitorId)){
            Map<String, Set<Session>> keyToSession = visitorToSession.get(visitorId);
            if(keyToSession.containsKey(key)){
                keyToSession.get(key).add(session);
            }else{
                Set<Session> sessions=Collections.synchronizedSet(new HashSet<>());
                keyToSession.put(key,sessions);
            }
        }else{
            Set<Session> sessions=Collections.synchronizedSet(new HashSet<>());
            Map<String,Set<Session>> keyToSession=Collections.synchronizedMap(new HashMap<>());
            keyToSession.put(key,sessions);
            visitorToSession.put(visitorId,keyToSession);
        }
        if(keyToVisitor.containsKey(key)){
            keyToVisitor.get(key).put(session,visitorId);
        }else{
            Map<Session,String> sessionToVisitor=Collections.synchronizedMap(new HashMap<>());
            sessionToVisitor.put(session,visitorId);
            keyToVisitor.put(key,sessionToVisitor);
        }

    }

    private static void destroySession(String key,Session session){
      
        String visitorId=keyToVisitor.get(key).get(session);
        if( visitorToSession.containsKey(visitorId)){
            Set<Session> sessions = visitorToSession.get(visitorId).get(key);
            sessions.remove(session);
            if(sessions.size()==0){
                visitorToSession.get(visitorId).remove(key);
                if(visitorToSession.get(visitorId).keySet().size()==0){
                    visitorToSession.remove(visitorId);
                }

            }
            
        }

        if(keyToVisitor.containsKey(key)){
            Map<Session,String> sessionToVisitor=keyToVisitor.get(key);
            if(sessionToVisitor.containsKey(session)){
                sessionToVisitor.remove(session);
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("key") String key) {
        regeisterSession(key,session,config);
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

    @OnError
    public void onError(Session session, Throwable error) {

    }

    /**
     * 推送广播信息
     * @param key
     * @param message
     * @param pushMessageCallback
     */
    public static void  pushBroadcast(String key,String message,IPushMessageCallback pushMessageCallback){
        pushMessage(key,message,keyToVisitor.get(key).keySet(),pushMessageCallback);

    }

    /**
     * 推送指定信息
     * @param visitorId
     * @param key
     * @param message
     * @param pushMessageCallback
     */
    public static void  pushAppoint(String visitorId,String key,String message,IPushMessageCallback pushMessageCallback){
       if(visitorToSession.containsKey(visitorId)){
           Set<Session> sessions = visitorToSession.get(visitorId).get(key);
           message=visitorId.concat(MESSAGE_CONCAT_MESSAGE).concat(key).concat(MESSAGE_CONCAT_MESSAGE).concat(message);
           pushMessage(key,message,sessions,pushMessageCallback);
       }else{
           pushMessageCallback.callback(false);
       }
    }

    private static void pushMessage(String key,String message,Collection<Session>sessions,IPushMessageCallback pushMessageCallback){
        for( Session session:sessions){
            try {
                session.getBasicRemote().sendText(message);
                pushMessageCallback.callback(true);
            } catch (IOException e) {
                pushMessageCallback.callback(false);
                destroySession(key,session);
                logger.error(ExceptionUtils.getMessage(e));
            }
        }
    }

    /**
     * 获取所有注册推送服务的在线访问者的ID
     * @return
     */
    public static Set<String> getPushOnLineUsers(){
        return visitorToSession.keySet();
    }
}
