package my.test.websocket.use_handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메시지 스펙 정의
 */
// {type: "", "sender": "me", "receiver": akefoa333-wef32f-w3f-s3f-s3fs3f, "data": test...."}
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageSpec<T> {

    private String type;
    private String sender;
    private String receiver;
    private T data;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void newConnect() {
        this.type = "new";
    }

    public void closeConnect() {
        this.type = "close";
    }

}
