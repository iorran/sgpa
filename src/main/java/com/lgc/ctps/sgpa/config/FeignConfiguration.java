package com.lgc.ctps.sgpa.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.lgc.ctps.sgpa")
public class FeignConfiguration {

}
