package websocket.messages;

public class ErrorMessage extends ServerMessage{
    private final String errMsg;

    public ErrorMessage(String errMsg) {
        super(ServerMessageType.ERROR);
        this.errMsg = errMsg;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}
