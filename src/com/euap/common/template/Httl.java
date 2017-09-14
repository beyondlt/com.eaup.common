package com.euap.common.template;

import com.euap.common.runtime.Envi;
import httl.Engine;
import httl.Template;

import java.io.IOException;
import java.text.ParseException;
import java.util.Properties;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public class Httl {

    private static Engine ENGINE;

    private static Engine DEV_ENGINE;

    static {
        Properties config = new Properties();
        Properties devConfig = new Properties();
        // 生产环境引擎
        config.put("compiler", "httl.spi.compilers.JavassistCompiler");
        config.put("json.codec", "httl.spi.codecs.JacksonCodec");
        config.put("cache.capacity", "1000");
        config.put("loggers", "httl.spi.loggers.Log4jLogger");
        config.put("output.stream", "false");
//        config.put("loaders+", "httl.spi.loaders.JarLoader");
//        config.put("loaders+", "httl.spi.loaders.ClasspathLoader");
//        config.put("loaders", "httl.spi.loaders.FileLoader");
//        config.put("loaders+", "httl.spi.loaders.MultiLoader");
        ENGINE = Engine.getEngine("product", config);

        // 开发环境引擎
        devConfig.putAll(config);
        devConfig.put("cache.capacity", "0");
        devConfig.put("reloadable", "true");
//
//        // 测试使用pan
//        devConfig.put("lint.unchecked", "true");
        devConfig.put("logger.level", "DEBUG");
        DEV_ENGINE = Engine.getEngine("dev", devConfig);
    }

    private static Engine getEngine() {
        return Envi.isDev() ? DEV_ENGINE : ENGINE;
    }

    /**
     * 从template路径读取模板
     *
     * @param templatePath
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static Template getTemplate(String templatePath) throws IOException, ParseException {
        return getEngine().getTemplate(templatePath, "UTF-8");
    }
}
