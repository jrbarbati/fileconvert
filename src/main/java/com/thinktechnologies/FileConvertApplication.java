package com.thinktechnologies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class FileConvertApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(FileConvertApplication.class, args);
    }
}
