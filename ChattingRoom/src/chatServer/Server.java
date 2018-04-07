package chatServer;

public class Server {

    public static void main(String[] args)
    {
        Thread text = new Thread(new TextServer());
        text.start();
        Thread talk=new Thread(new TalkServer());
        talk.start();
    }

}
