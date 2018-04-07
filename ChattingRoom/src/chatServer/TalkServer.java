package chatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class TalkServer implements Runnable {

    private ServerSocket listenSocket;
    private final static int DEFAULT_TALK_PORT = 8888;//端口号
    Vector<TalkConnection> talkConnections = new Vector<>();//在线客户

    public TalkServer()
    {
        serverListen();
    }
    private void serverListen()
    {
        try
        {
            listenSocket = new ServerSocket(DEFAULT_TALK_PORT);
            System.out.println("开始监听端口: " + DEFAULT_TALK_PORT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
    private void getListener()
    {
        TalkServer server=this;
        Thread thread = new Thread(){
            public void run(){
                try
                {
                    while (true)
                    {
                        Socket clientSocket = listenSocket.accept();
                        TalkConnection talkConnection=new TalkConnection(clientSocket,server);
                        talkConnections.add(talkConnection);
                        talkConnection.start();
                        System.out.println("new coming!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void run() {
        getListener();

    }

    public void removeConnection(TalkConnection talkConnection) {
        talkConnections.removeElement(talkConnection);
    }

    public void broadcastMsg(byte[] bts, TalkConnection talkConnection) {
        for(TalkConnection connection : talkConnections)
        {
            if(connection!=talkConnection)
            {
                connection.sendMsg(bts);
            }

        }
    }
}
