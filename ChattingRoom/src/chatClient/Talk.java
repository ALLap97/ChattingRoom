package chatClient;

import java.io.*;

import javax.sound.sampled.*;

public class Talk {

    private AudioFormat af;
    private TargetDataLine td ;
    //定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
    //定义字节数组输入输出流
    private ByteArrayInputStream bais;
    private ByteArrayOutputStream baos ;
    //定义音频输入流
    private AudioInputStream ais ;
    //定义停止录音的标志，来控制录音线程的运行
    private Boolean stopflag ;

    public Talk()
    {
        //定义录音格式
        af = null;
        //定义目标数据行,可以从中读取音频数据,该 TargetDataLine 接口提供从目标数据行的缓冲区读取所捕获数据的方法。
        td = null;
        //定义源数据行,源数据行是可以写入数据的数据行。它充当其混频器的源。应用程序将音频字节写入源数据行，这样可处理字节缓冲并将它们传递给混频器。
        //定义字节数组输入输出流
        bais = null;
        baos = null;
        //定义音频输入流
        ais = null;
        //定义停止录音的标志，来控制录音线程的运行
        stopflag = false;
    }
    public void capture()
    {
        try {
            //af为AudioFormat也就是音频格式
            af = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class,af);
            td = (TargetDataLine)(AudioSystem.getLine(info));
            //打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
            td.open(af);
            //允许某一数据行执行数据 I/O
            td.start();
            //创建播放录音的线程
            Record record = new Record();
            Thread t1 = new Thread(record);
            t1.start();

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            return;
        }
    }
    //停止录音
    public void stop()
    {
        stopflag = true;
    }
    //播放录音
    public void play()
    {
        //将baos中的数据转换为字节数据
        byte audioData[] = baos.toByteArray();
        //转换为输入流
        bais = new ByteArrayInputStream(audioData);
        af = getAudioFormat();
        ais = new AudioInputStream(bais, af, audioData.length/af.getFrameSize());
        try
        {

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally
        {
            try {
                //关闭流
                if(ais != null)
                {
                    ais.close();
                }
                if(bais != null)
                {
                    bais.close();
                }
                if(baos != null)
                {
                    baos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public ByteArrayOutputStream SendStream()
    {
        return baos;
    }
    //设置AudioFormat的参数
    public AudioFormat getAudioFormat()
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

    //录音类，因为要用到MyRecord类中的变量，所以将其做成内部类
    class Record implements Runnable
    {
        //定义存放录音的字节数组,作为缓冲区
        byte bts[] = new byte[2048];
        //将字节数组包装到流里，最终存入到baos中
        //重写run函数
        public void run() {
            baos = new ByteArrayOutputStream();
            try
            {
                System.out.print("recording...");
                stopflag = false;
                while(stopflag != true)
                {
                    //当停止录音没按下时，该线程一直执行
                    //从数据行的输入缓冲区读取音频数据。
                    //要读取bts.length长度的字节,cnt 是实际读取的字节数
                    int cnt = td.read(bts, 0, bts.length);
                    if(cnt > 0)
                    {
                        baos.write(bts, 0, cnt);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                System.out.print("  stop!\n");
                if(baos != null)// 关闭打开的字节数组流
                {
                    try {
                        baos.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                td.close();
            }
        }
    }
}
