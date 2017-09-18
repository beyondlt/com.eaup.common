package com.euap.common.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Filter;

@Configuration
@EnableRedisHttpSession
public class WebMvcConfiguration extends WebMvcConfigurerAdapter  {


	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 添加web拦截器
		registry.addInterceptor(new WebHandlerInterceptorAdapter());
	}

	@Bean
	public CookieHttpSessionStrategy cookieHttpSessionStrategy() {
		CookieHttpSessionStrategy strategy = new CookieHttpSessionStrategy();
		strategy.setCookieSerializer(new CrossCookieSerializer());
		return strategy;
	}

	@Bean
	public  Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter =new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}
	
	@Bean
	public StringHttpMessageConverter utf8StringHttpMessageConverter(){
		UTF8StringHttpMessageConverter utf8=new UTF8StringHttpMessageConverter();
		return utf8;
	}
//	 @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 多个拦截器组成一个拦截器链
//        // addPathPatterns 用于添加拦截规则
//        // excludePathPatterns 用户排除拦截
//        registry.addInterceptor(new WebSocketInterceptor()).addPathPatterns("/websocket/*");
//       
//        super.addInterceptors(registry);
//    }

}
