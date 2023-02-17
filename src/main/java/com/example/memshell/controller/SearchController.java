package com.example.memshell.controller;

import com.example.memshell.josearcher.entity.Keyword;
import com.example.memshell.josearcher.searcher.SearchRequstByBFS;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/app")
public class SearchController {
    @RequestMapping("/search")
    @ResponseBody
    public void testDemo(){
        List<Keyword> keys = new ArrayList<>();
        Keyword.Builder builder = new Keyword.Builder();
        builder.setField_type("nnn");
        keys.add(new Keyword.Builder().setField_type("ServletRequest").build());
        keys.add(new Keyword.Builder().setField_type("RequstGroup").build());
        keys.add(new Keyword.Builder().setField_type("RequestInfo").build());
        keys.add(new Keyword.Builder().setField_type("RequestGroupInfo").build());
        keys.add(new Keyword.Builder().setField_type("Request").build());
        //新建一个广度优先搜索Thread.currentThread()的搜索器
        SearchRequstByBFS searcher = new SearchRequstByBFS(Thread.currentThread(),keys);
        //打开调试模式
        searcher.setIs_debug(true);
        //挖掘深度为20
        searcher.setMax_search_depth(50);
        //设置报告保存位置
        searcher.setReport_save_path("C:\\Users\\bmth\\Desktop\\作业\\CTF学习\\java学习");
        searcher.searchObject();
    }
}