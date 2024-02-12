package my.test.websocket.use_stomp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StompMessagePiece {
    private Long roomId;
    private Object data;
    private String sender;
}
