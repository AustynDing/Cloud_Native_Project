package com.example.cloud_native_nju27.service;

import com.example.cloud_native_nju27.domain.Nju27;
import org.springframework.stereotype.Service;

@Service
public class Nju27Service {
    public Object nju27(){
        return new Nju27("Hello, we are Nju27!");
    }
}
