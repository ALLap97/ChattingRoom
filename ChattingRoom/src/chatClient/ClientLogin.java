package chatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientLogin extends JFrame {

    private String ip;
    private String clientName;
    private final JLabel address = new JLabel("IP地址：");
    private final JTextField f1 = new JTextField(18);
    private JLabel name = new JLabel("客户名：");
    private final JTextField f2 = new JTextField(18);
    private JLabel welcome=new JLabel("Welcome!");
    private JButton get_in = new JButton("进入");
    public ClientLogin()
    {
        init();
        ip="";
        clientName="";
    }
    private void judge()
    {
        //new Client("127.0.0.1","pp1");
        //hide();
        clientName = f2.getText();
        ip = f1.getText();
        String regEx = "^[A-Za-z][A-Za-z1-9_-]+$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(clientName);
        boolean rs = matcher.matches();
        if (!rs) {
            JOptionPane.showMessageDialog(getContentPane(),
                    "用户名必须是字母开头 + 数字/字母/下划线", "提示信息", JOptionPane.INFORMATION_MESSAGE);
        } else {
            new Client(ip,clientName);

            hide();
        }
    }
    private void init()
    {
        this.setTitle("登录");
        this.setSize(300,200);
        this.add(address);
        this.add(f1);
        this.add(name);
        this.add(f2);
        this.add(welcome);
        this.add(get_in);
        this.setLayout(new FlowLayout());
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        get_in.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                judge();
            }
        });
        f2.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    judge();
                }
            }
        });
    }
}
