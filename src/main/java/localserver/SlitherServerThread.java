package localserver;

import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;


public class SlitherServerThread extends Thread {

    public SlitherServerThread(){
        System.out.println("Initiating Server Thread.");
    }

    @Override
    public void run() {
        WebSocketServer server = new SlitherServer(new InetSocketAddress("localhost", 8887));
        server.run();
    }
}
