package com.atguigu.gulimail.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {


    @GetMapping("/confirm.html")
    public String confirmPage() {
        return "confirm";
    }

    @GetMapping("/detail.html")
    public String detailPage() {
        return "detail";
    }


    @GetMapping("/list.html")
    public String listPage() {
        return "list";
    }

    @GetMapping("/pay.html")
    public String payPage() {
        return "pay";
    }
}
