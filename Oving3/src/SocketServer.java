import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private final int PORT = 1250;
    private final MessageHandler messageHandler;

    public SocketServer() throws IOException {
        Socket socket = start();
        messageHandler = new MessageHandler(socket);
        run();
        messageHandler.close();
    }

    private Socket start() throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Starting server...");
        return server.accept(); // Waiting for contact
    }

    private void run() {
        System.out.println("A client connected to the server!");
        String message = messageHandler.readMessage();
        System.out.println("A client connected to the server!");
        while(message != null) {
            System.out.println(message);
            try {
                int a = Integer.parseInt(message);
                a += 10;
                messageHandler.sendMessage(Integer.toString(a));
                System.out.println("Sent the number + 10");
            } catch (Exception e) {
                // Nothing. To handle. If its not a number, we're not
                // interested right now
                System.out.println("That wasn't a number!");
                if(message.equals("close")) {
                    System.out.println("Shutting down...");
                    System.exit(0);
                }
                messageHandler.sendMessage("");
            }
            message = messageHandler.readMessage();
        }

    }
}
