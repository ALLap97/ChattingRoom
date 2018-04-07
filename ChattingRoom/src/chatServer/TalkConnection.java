package chatServer;

import java.io.*;
import java.net.Socket;

public class TalkConnection extends Thread {
    private Socket client;
    private DataInputStream is;
    private DataOutputStream os;
    private TalkServer server;
    private boolean connected;
    public TalkConnection(Socket clientSocket, TalkServer talkServer)
    {
        client = clientSocket;
        server = talkServer;
        connected=true;
        try
        {
            is=new DataInputStream(client.getInputStream());
            os =new DataOutputStream(client.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public boolean disconnected()
    {
        return client.isClosed();
    }
    public void run()
    {
        while(!disconnected())
        {
            byte bts[]=receiveMsg();
            if(over(bts)) break;
            server.broadcastMsg(bts,this);
        }
        closeConnection();
        System.out.println("close!");
        server.removeConnection(this);
    }

    public void closeConnection()
    {
        try
        {
            is.close();
            os.close();
            client.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendMsg(byte b[])
    {
        try {
            os.write(b);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] receiveMsg()
    {
        byte bts[]=new byte[204800];
        try
        {
            is.read(bts);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bts;
    }

    private boolean over(byte[] bts)
    {
        for(int i=0;i<100;i++)
        {
            if(bts[i]!=127)
            {
                return false;
            }
        }
        return true;
    }

}
