package com.hmdp.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {

    @Value("${wenxin.api.key}")
    private String apiKey;

    @Value("${wenxin.api.base-url:https://api.openai.com/v1}")
    private String baseUrl;

   
}