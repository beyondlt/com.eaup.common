package com.euap.common.entity;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.Serializable;

import java.util.Map;

/**
 * 服务响应类
 * @author         刘涛
 */
public class ServiceBody implements Serializable {

    public final static String  LINE_BREAKS="<br>";

    /** 操作是否成功 */
    boolean success = true;

    /** 操作返回的数据 */
    Object result;

    /** 实体对象属性的验证信息 */
    Map<String, String> errors;

    /** 操作中产生的产生的错误信息 */
    String message;

    /** Field description */
    int count;

    /**
     * Method description
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Method description
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Method description
     * @param errorMessage
     */
    public void setException(String errorMessage) {
        this.success = false;

        this.message="操作失败！原因：" +errorMessage.replace("\r\n", "<br>");

    }

    /**
     * Method description
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Method description
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Method description
     * @return
     */
    public Object getResult() {
        return result;
    }

    /**
     * Method description
     * @param result
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * Method description
     * @param e
     * @return
     */
    private Throwable getRootException(Throwable e) {
        Throwable t = e.getCause();

        if (t != null) {
            return getRootException(t);
        } else {
            return e;
        }
    }

    /**
     * Method description
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Method description
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
