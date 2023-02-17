package com.example.memshell.Memdemo.resin;

import javassist.ClassPool;

import java.util.Base64;

class ToEncode {
    public static void main(String[] argv) throws Exception{
        //转换为字节码并编码为bcel字节码
//        JavaClass cls = Repository.lookupClass(Resin_echo1.class);
//        String code = Utility.encode(cls.getBytes(), true);
//        System.out.println(code);

        byte[] bytes = ClassPool.getDefault().get(Resin_echo.class.getName()).toBytecode();
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }
}
