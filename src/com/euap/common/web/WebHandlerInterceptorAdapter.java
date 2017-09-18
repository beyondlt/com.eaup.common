package com.euap.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebHandlerInterceptorAdapter extends HandlerInterceptorAdapter  {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {


		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		response.setHeader("Access-Control-Allow-Credentials","true"); //是否支持cookie跨域
		String contextPath =request.getContextPath();
		String url=request.getRequestURL().toString();
		int index=url.indexOf(contextPath);
		contextPath=url.substring(0, index+contextPath.length());
		if(contextPath.startsWith("https")){
			request.setAttribute("wsapp", contextPath.replaceAll("https", "wss"));
		}else{
			request.setAttribute("wsapp", contextPath.replaceAll("http", "ws"));
		}
		request.setAttribute("webapp", contextPath);
		RequestThreadLocal.set(request);
		return true;
	}



	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}




}
