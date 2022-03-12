package com.hugmount.ajiao.config;

import com.hugmount.ajiao.proxy.JdkProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Li Huiming
 * @date 2022/3/12
 */
@Configuration
@ComponentScan("com.hugmount")
public class SpringBootStarterConfig {

    @Bean
    public JdkProxy jdkProxy() {
        return new JdkProxy();
    }

}
