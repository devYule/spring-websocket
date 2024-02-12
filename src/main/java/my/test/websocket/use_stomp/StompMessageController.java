package my.test.websocket.use_stomp;

import lombok.RequiredArgsConstructor;
import my.test.websocket.use_handler.MessageSpec;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StompMessageController {


    /**
     * StompMessageSpec 의 스펙에 맞추어 JSON 으로 전송된 요청을 messageSpec 객체로 파싱한다.
     * {roomId} 에 해당하는 변수는 Long roomId 로 파싱된다 (@DestinationVariable Long roomId)
     * 즉, /send/1 로 요청시 데이터가 StompMessagePiece 로 변환되어 /chat/1 을 구독하고 있는 모든 사용자에게 전달 된다!
     * @param roomId
     * @param messageSpec
     * @return StompMessagePiece
     */
    @MessageMapping("/{roomId}")
    // config 에서 prefix 로 설정한것은 생략된다. 해당 prefix 가 앞에 붙으면 자동으로 @MessageMapping 을 찾게 된다.
    // 실제로는 prefix 가 추가되어 /send/{roomId} 로 전송해야 한다.
    @SendTo("/chat/{roomId}")
    // /chat/{roomId} 를 구독한 모든 유저에게 메시지가 전달 된다.
    public StompMessagePiece send(@DestinationVariable Long roomId, StompMessageSpec messageSpec) {
        // StompMessagePiece 에서는 필요한 메시지, 메시지 작성자 (sender), roomId 를 담아 리턴한다.
        return new StompMessagePiece(roomId, messageSpec.getData(), messageSpec.getSender());
    }


}
