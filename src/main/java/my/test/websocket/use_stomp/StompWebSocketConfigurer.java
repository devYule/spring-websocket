package my.test.websocket.use_stomp;

import jdk.jfr.Frequency;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 설정 등록
 * 메시지 브로커를 사용 (활성화)
 * 엔드포인트 등록과 메시지 브로커 설정을 담당.
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfigurer implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-stomp") // SockJS 연결 주소
                .withSockJS(); // 만약 websocket 을 사용할 수 없는 브라우저라면 다른 방식을 사용하도록 설정
        // 주소: ws://localhost:8080/ws-stomp
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat");
        // /chat/** 은 모두 구독 url 로 설정, 메시지 브로커가 동작.
        // 컨트롤러의 반환 객체가 해당 구독자들에게 전송됨
        registry.setApplicationDestinationPrefixes("/send");
        // /send/** 으로 메시지를 보내면, @MessageMapping 이 붙은 곳으로 간다.
        // 그 이후, @MessageMapping("...") 의 ... 에 해당하는 url 로 찾아간다.
        // ex) /send/1 -> @MessageMapping(/{roomId})
        // 이 @MessageMapping 이 붙은 컨트롤러에서 메시지 가공이 가능하다.
    }


}
