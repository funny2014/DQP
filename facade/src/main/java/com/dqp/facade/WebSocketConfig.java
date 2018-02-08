package com.dqp.facade;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
* websocket的配置
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");

        //以下为stomp js 方式接收消息时，分布式需要配置
//        config.enableStompBrokerRelay("/topic") // 设置可以订阅的地址，也就是服务器可以发送的地址
//                .setRelayHost(ConfigureUtil.getProperty("BrokerUrl")).setRelayPort(Integer.valueOf(ConfigureUtil.getProperty("BrokerPort"))) // 设置broker的地址及端口号
//                .setSystemHeartbeatReceiveInterval(2000) // 设置心跳信息接收时间间隔
//                .setSystemHeartbeatSendInterval(2000); // 设置心跳信息发送时间间隔
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/hello").withSockJS();
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

}
