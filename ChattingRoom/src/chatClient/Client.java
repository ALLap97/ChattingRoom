package chatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Client extends JFrame{

    private JTextField txtInput = new JTextField("", 20);
    private JButton btnSend = new JButton("发送");
    private JButton btnClose=new JButton("关闭");
    private JButton btnTalk=new JButton("说话");
    private JButton btnStop=new JButton("暂停");
    private JButton btnPlay=new JButton("播放");
    private JButton btnSendTalk=new JButton("发送");
    private JList<String> lstMsg = new JList<>();
    private DefaultListModel<String> lstMsgModel = new DefaultListModel<>();
    private JList<String> NameMsg = new JList<>();//在线人集合
    private DefaultListModel<String>NameMsgModel = new DefaultListModel<>();

    private String ip;
    private String clientName;
    private Connection textConnection;
    private Connection talkConnection;
    private Talk talk;
    public Client(String ip,String clientName)
    {
        this.ip=ip;
        this.clientName=clientName;
        init();
        getConnected();
        getMessage();
    }

    private void init()
    {

        this.setSize(500, 500);
        this.setTitle(clientName);
        NameMsg.setModel(NameMsgModel);
        JScrollPane namePane=new JScrollPane(NameMsg);
        namePane.setPreferredSize(new Dimension(100,0));
        getContentPane().add(namePane,BorderLayout.EAST);
        //显示在线用户
        JScrollPane MsgPane=new JScrollPane(lstMsg);
        //MsgPane.setPreferredSize(new Dimension(250, 0));
        lstMsg.setModel(lstMsgModel);
        getContentPane().add(MsgPane,BorderLayout.CENTER);
        //中心显示消息面板
        JPanel Foot=new JPanel();
        Foot.setLayout(new GridLayout(2, 1));

        JPanel pnlFoot = new JPanel();
        pnlFoot.add(txtInput);
        pnlFoot.add(btnSend);
        pnlFoot.add(btnClose);
        Foot.add(pnlFoot);

        JPanel pnlFoot2 = new JPanel();
        pnlFoot2.setLayout(new GridLayout(1, 4));
        pnlFoot2.add(btnTalk);
        pnlFoot2.add(btnStop);
        pnlFoot2.add(btnPlay);
        pnlFoot2.add(btnSendTalk);
        btnTalk.setEnabled(true);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        btnSendTalk.setEnabled(false);
        Foot.add(pnlFoot2);

        btnSend.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                // TODO Auto-generated method stub
                if( txtInput.getText().length() != 0 )
                {
                    textConnection.sendTextMessage(clientName+":"+txtInput.getText() );
                    txtInput.setText("");
                }
            }
        });

        txtInput.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode()==10)
                {
                    if(txtInput.getText().length() != 0 )
                    {
                        if( txtInput.getText().length() != 0 )
                        {
                            textConnection.sendTextMessage(clientName+":"+txtInput.getText() );
                            txtInput.setText("");
                        }
                    }
                }
            }

        });

        btnTalk.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    btnTalk.setEnabled(false);
                    btnStop.setEnabled(true);
                    btnPlay.setEnabled(false);
                    btnSendTalk.setEnabled(false);
                    talk=new Talk();
                    talk.capture();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        });


        btnStop.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    btnTalk.setEnabled(true);
                    btnStop.setEnabled(false);
                    btnPlay.setEnabled(true);
                    btnSendTalk.setEnabled(true);
                    talk.stop();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }

        });
        btnPlay.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    talk.play();
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }

        });

        btnSendTalk.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    textConnection.sendTextMessage(clientName+" is talking:");
                    talkConnection.sendTalkMessage(talk.SendStream());
                } catch(Exception e2)
                {
                    processMsg("发送失败");
                }
            }
        });

        getContentPane().add(Foot, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeConnection();
            }
        });

        this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                closeConnection();
            }
        });

    }

    private void getConnected()
    {
        try {
            textConnection=new TextConnection(ip,clientName);
            talkConnection = new TalkConnection(ip,clientName);
        } catch (IOException e) {
            processMsg("连接失败");
        }
    }

    private void closeConnection()
    {
        try
        {

            textConnection.sendOverMessage();
            textConnection.closeConnection();
            talkConnection.sendOverMessage();
            talkConnection.closeConnection();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        finally
        {
            System.exit(0);
        }
    }
    private void getMessage()
    {
        Thread textThread = new Thread(){
            public void run(){
                while(textConnection.connected())
                {
                    String message=textConnection.receiveTextMessage();
                    if(message!=null)
                    {
                        processMsg(message);
                    }
                }
                processMsg( "与服务器连接中断，请检查网络连接" );
            }
        };
        textThread.start();

        Thread talkThread = new Thread(){
            public void run(){
                while(talkConnection.connected())
                {
                    talkConnection.receiveTalkMessage();
                }
                processMsg( "与服务器连接中断，请检查网络连接" );
            }
        };
        talkThread.start();
    }

    private void processMsg(String message)
    {
        lstMsgModel.addElement(message);
    }

}

