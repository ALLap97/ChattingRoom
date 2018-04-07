package chatClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface Connection {
    public void startConnection( ) throws IOException;
    public void sendTextMessage(String message);
    public String receiveTextMessage();
    public void sendTalkMessage(ByteArrayOutputStream baos);
    public void receiveTalkMessage();
    public void sendOverMessage();
    public void closeConnection();
    public boolean connected();
}
