package com.example.simplejpa;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Hello {
    public String hello() {
        return "Hello From Injected object!";
    }
}
