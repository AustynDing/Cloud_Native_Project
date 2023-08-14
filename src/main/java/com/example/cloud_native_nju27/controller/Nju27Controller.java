package com.example.cloud_native_nju27.controller;

import com.example.cloud_native_nju27.controller.limit.Limit;
import com.example.cloud_native_nju27.service.Nju27Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Nju27Controller {

    @Autowired
    private Nju27Service nju27Service;

    @GetMapping("/nju27")
    @Limit(count = 100)
    public Object nju27() {
        return nju27Service.nju27();
    }
}
