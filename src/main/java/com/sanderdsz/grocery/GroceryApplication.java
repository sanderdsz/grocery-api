package com.sanderdsz.grocery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@SpringBootApplication
public class GroceryApplication {

	public static void main(String[] args) { SpringApplication.run(GroceryApplication.class, args); }
}

