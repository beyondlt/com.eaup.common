package com.euap.common.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class UTF8StringHttpMessageConverter extends StringHttpMessageConverter {

	private static final MediaType PLAIN_UTF8 = new MediaType("text", "plain", Charset.forName("UTF-8"));
	private static final MediaType HTML_UTF8 = new MediaType("text", "html", Charset.forName("UTF-8"));
	

	@Override
	protected void writeInternal(String s, HttpOutputMessage outputMessage) throws IOException {
		HttpHeaders header=outputMessage.getHeaders();
		header.setAcceptCharset(getAcceptedCharsets());
		MediaType contentType = outputMessage.getHeaders().getContentType();
		if(contentType.includes(MediaType.TEXT_HTML)){
			header.setContentType(HTML_UTF8);
			header.setContentLength(s.getBytes("UTF-8").length);
		}else if(contentType.includes(MediaType.TEXT_PLAIN)){
			header.setContentType(PLAIN_UTF8);
		}else{
			/**
			 * 在IE8下，第一次请求页面默认为img/jpg模式
			 * 强制转换为HTML_UTF-8模式
			 */
			header.setContentType(HTML_UTF8);
			header.setContentLength(s.getBytes("UTF-8").length);
		}
		StreamUtils.copy(s, Charset.forName("UTF-8"), outputMessage.getBody());

	}


}
