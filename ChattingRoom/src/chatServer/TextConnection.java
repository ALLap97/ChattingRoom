package chatServer;

import java.io.*;
import java.net.Socket;

public class TextConnection extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private TextServer server;
    private boolean connected;

    public TextConnection(Socket clientSocket, TextServer server)
    {
        this.clientSocket=clientSocket;
        this.server=server;
        connected=true;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean disconnected()
    {
        return clientSocket.isClosed();
    }
    public void sendMsg(String msg)
    {
        out.println( msg );
        out.flush();
    }
    public String receiveMsg()
    {
        String msg = null;
        try {
            if(!disconnected())
            {
                msg = in.readLine();
            }
        } catch (IOException e) {
            //e.printStackTrace();
            //System.out.println("TEXT非正常下线!");
            msg=null;
            connected=false;
        }
        return msg;
    }
    public void closeConnection()
    {
        try
        {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void run()
    {
        while(!disconnected()&&connected)
        {
            String message = receiveMsg();
            if(message!=null)
            {
                server.processMsg(message);
                server.broadcastMsg(message);
            }
        }
        //System.out.println("TEXT close!");
        closeConnection();
        server.removeConnection(this);
    }
}
