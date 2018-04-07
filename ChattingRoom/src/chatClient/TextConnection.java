package chatClient;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TextConnection implements Connection {
    private   int DEFAULT_PORT = 8899;
    private   String ip;
    private String clientName;
    private Socket socket;
    BufferedReader in;
    PrintWriter out;

    public TextConnection(String ip,String Name) throws IOException {
        this.ip=ip;
        clientName=Name;
        startConnection();
    }
    @Override
    public void startConnection( ) throws IOException {

        socket = new Socket( ip, DEFAULT_PORT);
        in = new BufferedReader(
                new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(socket.getOutputStream());
        sendTextMessage(clientName+"上线了!");
    }

    @Override
    public void sendTextMessage(String message) {
        out.println( message );
        out.flush();
    }

    @Override
    public String receiveTextMessage() {
        String msg = null;
        try {
                msg = in.readLine();

        } catch (IOException e) {
            //e.printStackTrace();
            msg=null;
        }
        return msg;
    }

    @Override
    public void sendTalkMessage(ByteArrayOutputStream baos) {

    }

    @Override
    public void receiveTalkMessage() {

    }

    @Override
    public void sendOverMessage() {
        String over=clientName+"下线!";
        out.println( over );
        out.flush();
    }

    @Override
    public void closeConnection() {
        if(connected())
        {
            try
            {
                socket.close();
                in.close();
                out.close();

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean connected() {
        return !socket.isClosed();
    }
}
