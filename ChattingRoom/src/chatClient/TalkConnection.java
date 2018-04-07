package chatClient;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TalkConnection implements Connection{

    private Socket socket;
    private DataOutputStream os;
    private DataInputStream is;
    private ByteArrayInputStream bais ;
    private AudioInputStream ais;
    private int DEFAULT_PORT = 8888;
    private String ip;
    private String clientName;

    public TalkConnection(String ip,String Name) throws IOException {
        this.ip=ip;
        clientName=Name;
        startConnection();
    }
    @Override
    public void startConnection() throws IOException {

        socket = new Socket( ip, DEFAULT_PORT);
        is=new DataInputStream(socket.getInputStream());
        os =new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void sendTextMessage(String message) {

    }

    @Override
    public String receiveTextMessage() {
        return null;
    }

    @Override
    public void sendTalkMessage(ByteArrayOutputStream baos) {
        try
        {
            os.write(baos.toByteArray());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendOverMessage()
    {
        try
        {
            byte []over=new byte[100];
            for(int i=0;i<100;i++)
            {
                over[i]=127;
            }
            os.write(over);
            os.flush();
            closeConnection();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }


    @Override
    public void receiveTalkMessage() {
        byte []audioData=new byte[204800];
        try
        {
            is.read(audioData);
            bais = new ByteArrayInputStream(audioData);
            AudioFormat format = getAudioFormat(1);
            ais = new AudioInputStream(bais, format, audioData.length/format.getFrameSize());
            try
            {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
            catch (LineUnavailableException e)
            {
                e.printStackTrace();
            }
        } catch (IOException e1) {
           // e1.printStackTrace();

        }
    }

    @Override
    public void closeConnection() {
        if(connected())
        {
            try
            {
                socket.close();
                is.close();
                os.close();

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
    public AudioFormat getAudioFormat(int type)
    {
        if(type==1)
        {
            AudioFormat.Encoding encoding = AudioFormat.Encoding.
                    PCM_SIGNED ;
            float rate = 8000f;
            int sampleSize = 16;
            boolean bigEndian = true;
            int channels = 1;
            return new AudioFormat(encoding, rate, sampleSize, channels,
                    (sampleSize / 8) * channels, rate, bigEndian);
        }
        else
        {
            //采样率是每秒播放和录制的样本数
            float sampleRate = 16000.0F;
            // 采样率8000,11025,16000,22050,44100
            //sampleSizeInBits表示每个具有此格式的声音样本中的位数
            int sampleSizeInBits = 16;
            // 8,16
            int channels = 1;
            // 单声道为1，立体声为2
            boolean signed = true;
            // true,false
            boolean bigEndian = true;
            // true,false
            return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
        }
    }
}
