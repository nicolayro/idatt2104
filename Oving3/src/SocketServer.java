import java.io.IOException;
import java.net.ServerSocket;

public class SocketServer {
    private static final int PORT = 1250;
    private ServerSocket serverSocket;

    public SocketServer() throws IOException {
        start();
        run();
    }

    private void start() throws IOException {
        System.out.println("Starting server...");
        this.serverSocket = new ServerSocket(PORT);
    }

    public void run() {
        while(true) {
            try {
                ServerThread t = new ServerThread(serverSocket.accept());
                t.start();
            } catch (Exception e) {
                System.out.println("There was an error connection to the client");
                e.printStackTrace();
            }
        }
    }
}
