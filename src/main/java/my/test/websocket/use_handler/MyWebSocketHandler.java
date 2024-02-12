package my.test.websocket.use_handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 4개의 메소드 오버라이딩 필요.
 * - afterConnectionEstablished : 웹소켓 연결 시
 * - handleTextMessage : 데이터 통신 시
 * - afterConnectionClosed : 웹소켓 연결 종료 시
 * - handleTransportError : 웹소켓 통신 에러 시
 */
@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();


    /**
     * 최초 연결시
     * 나를 제외한 모든 접속유저에게 메시지 보내기 예제
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 세션 저장
        ObjectMapper om = new ObjectMapper();
        String sessionId = session.getId();
        this.sessions.put(sessionId, session);

        // 메시지 작성
        MessageSpec message = MessageSpec.builder().sender(sessionId).receiver("all").build();
        message.newConnect();

        // 메시지 전송 (본인일 경우에는 보내지 않음, 보내는 방법은 json)
        this.sessions.values().forEach(s -> {
            try {
                if (!s.getId().equals(sessionId)) {
                    s.sendMessage(new TextMessage(om.writeValueAsString(message)));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

    }

    /**
     * 데이터 통신 - 메시지 전송시 호출되는 메소드
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper om = new ObjectMapper();
        // TextMessage message 파라미터는 JSON 타입으로 보낸 메시지가 기입된다. - debug check
        // 실제 전달된 JSON 이 그대로 필드화 되어 넘어온다
        // 바이트 타입으로 넘어오므로, 내가 정의한 메시지의 스펙 (MessageSpec) 과 동일한 JSON 이라면 ObjectMapper 로 바인딩이 가능하다.
        MessageSpec<Object> messageSpec = om.readValue(message.getPayload(), MessageSpec.class);
        messageSpec.setSender(session.getId()); // 메시지의 리시버 찾기 (연결된 sessions 에서)
        WebSocketSession receiver = sessions.get(messageSpec.getReceiver());

        if (receiver != null && receiver.isOpen()) { // 존재하는 session 이며, 유효하면 메시지 전송
            receiver.sendMessage(new TextMessage(om.writeValueAsString(messageSpec)));
        }
    }

    /**
     * 연결 종료시
     * 접속된 모든 사용자에게 접속 종료 메시지 보내기
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ObjectMapper om = new ObjectMapper();
        String sessionId = session.getId();

        sessions.remove(sessionId); // session 을 보관하고 있는 sessions 에서 해당 sessionId 를 통해 session 제거

        // 마지막 메시지 작성
        final MessageSpec message = new MessageSpec();
        message.closeConnect(); // 상태를 close 로 바꿈 (메시지의 상태임 - 즉, 마지막 메시지라는것을 파악할 수 있음.)
        message.setSender(sessionId);

        // sessions 에 보관된 모든 접속중인 session 에 메시지 보내기 (본인은 위에서 이미 remove 했음)
        sessions.values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(om.writeValueAsString(message)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 에러 발생시
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.debug("sessionId = {}", session.getId());
        log.debug("session = {}", session);
        log.debug("ex", exception);
        throw new RuntimeException();
    }


}
