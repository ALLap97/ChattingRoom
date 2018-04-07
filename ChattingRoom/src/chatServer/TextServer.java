package chatServer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class TextServer extends JFrame implements Runnable{

    private JList<String> lstMsg = new JList<>();//对话集合
    private DefaultListModel<String> lstMsgModel = new DefaultListModel<>();
    private JList<String> NameMsg = new JList<>();//在线人集合
    private DefaultListModel<String>NameMsgModel = new DefaultListModel<>();

    private final static int DEFAULT_TEXT_PORT = 8899;//端口号
    private ServerSocket listenSocket;
    private Vector<TextConnection> textConnections=new Vector<>();//在线的连接

    public TextServer()
    {
        try
        {
            init();
            serverListen();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void init() throws Exception
    {
        NameMsg.setModel(NameMsgModel);
        JScrollPane namePane=new JScrollPane(NameMsg);
        namePane.setPreferredSize(new Dimension(110,0));
        getContentPane().add(namePane, BorderLayout.EAST);
        //右边显示姓名面板
        JScrollPane MsgPane=new JScrollPane(lstMsg);
        //MsgPane.setPreferredSize(new Dimension(250, 0));
        lstMsg.setModel(lstMsgModel);
        getContentPane().add(MsgPane,BorderLayout.CENTER);
        //中心显示消息面板
        this.setTitle("聊天服务器");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(500, 400);
    }

    public void serverListen()
    {
        try
        {
            listenSocket = new ServerSocket(DEFAULT_TEXT_PORT);
            processMsg("开始监听端口: " + DEFAULT_TEXT_PORT);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getListener()
    {
        TextServer server=this;
        Thread thread = new Thread(){
            public void run(){
                try
                {
                    while (true)
                    {
                        Socket clientSocket = listenSocket.accept();
                        TextConnection textConnection=new TextConnection(clientSocket,server);
                        textConnections.add(textConnection);
                        textConnection.start();
                        processMsg("new coming!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    public void processMsg(String message)
    {
        lstMsgModel.addElement(message);
    }
    public void broadcastMsg(String message)
    {
        for(TextConnection connection : textConnections)
        {
            connection.sendMsg(message);
        }
    }
    public void removeConnection(TextConnection connection)
    {
        textConnections.removeElement(connection);
    }
    public void run()
    {
        getListener();
    }

}

