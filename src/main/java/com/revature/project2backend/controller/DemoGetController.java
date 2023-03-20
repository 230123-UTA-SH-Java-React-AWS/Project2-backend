package com.revature.project2backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class DemoGetController {

    @GetMapping("hello")
    public String hello(Principal principal){
        return "Hello " + principal.getName();
    }
}
