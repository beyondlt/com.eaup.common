package com.euap.common.template;

import com.euap.common.utils.JacksonUtil;
import com.euap.common.web.RequestThreadLocal;
import org.apache.commons.lang.StringUtils;

import java.io.CharArrayWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public class Vue {

    private String template;

    private String[] pageScript;

    private String[] component;

    private String[] lib;

    private String[] css;

    private Class[] entriyClass;


    private Vue(){};



    public Vue(String template){
        this.template=template;
    }

    public Vue registerlibrary(String[] lib){
        this.lib=lib;

        return this;
    }
    public Vue registerStyle(String[] css){
        this.css=css;
        return this;
    }
    public Vue registerComponent(String[] component){
        this.component=component;
        return this;
    }

    public Vue registerPageScript(String[] pageScript){
        this.pageScript=pageScript;
        return this;
    }

    public Vue registerEntity(Class[] entityClass){
        this.entriyClass=entityClass;
        return this;
    }

    public String toHtml(){
       return toHtml(null);
    }

    public String toHtml(Map model){
        if(null==model){
            model=new HashMap();
        }
        model.put("webapp", RequestThreadLocal.getWebappUrl());
        model.put("wsapp",RequestThreadLocal.getWsappUrl());
        model.put("styles",css);
        model.put("plugs",lib);

        String html = "";
        Writer templateWrite=null;
        Writer pageScriptWrite=null;
        Writer componentWrite=null;
        Writer htmlWrite = null;
        try{
            templateWrite = new CharArrayWriter();
            if (StringUtils.isNotBlank(template)) {
                Httl.getTemplate(template).render(model,templateWrite);
            }
            templateWrite.flush();
            model.put("template",templateWrite.toString());

            componentWrite=new CharArrayWriter();
            if (null != component) {
                for (String c : component) {
                    Httl.getTemplate(c).render(model, componentWrite);
                    componentWrite.write("\r\n\t");
                }
            }
            componentWrite.flush();
            model.put("component", componentWrite.toString());

            pageScriptWrite=new CharArrayWriter();
            if (null != pageScript) {
                for (String ps : pageScript) {
                    Httl.getTemplate(ps).render(model, pageScriptWrite);
                    pageScriptWrite.write("\r\n\t");
                }

            }
            pageScriptWrite.flush();
           StringBuilder entityBuilder=new StringBuilder();
            for(Class t:entriyClass){
                Object o=t.newInstance();
                entityBuilder.append("var ".concat(t.getSimpleName()).concat("=").concat(JacksonUtil.getObjectMapper().writeValueAsString(o)));
                entityBuilder.append("\r\n\t");
            }

            model.put("pageScript",entityBuilder.toString().concat(pageScriptWrite.toString()));
            htmlWrite = new CharArrayWriter();
            Httl.getTemplate("vue.html").render(model, htmlWrite);
            htmlWrite.flush();
            html = htmlWrite.toString();
            htmlWrite.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(templateWrite!=null){
               try{
                   templateWrite.close();
               }catch (Exception e){
                   e.printStackTrace();
               }
                templateWrite=null;
            }
            if(pageScriptWrite!=null){
                try{
                    pageScriptWrite.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                pageScriptWrite=null;
            }
            if(htmlWrite!=null){
                try{
                    htmlWrite.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                htmlWrite=null;
            }
        }
        return html;
    }
}
