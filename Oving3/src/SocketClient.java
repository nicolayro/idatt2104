import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


class SocketClient {
    private final int PORT = 1250;
    private final MessageHandler messageHandler;
    private final Scanner input;

    public SocketClient() throws IOException {
        input = new Scanner(System.in);
        Socket socket = start();
        messageHandler = new MessageHandler(socket);
        run();
        messageHandler.close();
    }

    private Socket start() {
        while(true) {
            try {
                // Reading host machine
                System.out.println("Name of server machine: ");
                String serverHost = input.nextLine();

                // Setting up the client
                Socket connection = new Socket(serverHost, PORT);
                System.out.println("Connection created successfully");
                return connection;
            } catch (IOException e) {
                System.out.println("Please enter a valid name, like \"localhost\"");
            }
        }
    }

    private void run() {
        System.out.println("\nYou've successfully connected with the server!");
        System.out.println("Say hi!");
        String message = input.nextLine();
        while(message != null) {
            if(message.equals("close")) return;
            messageHandler.sendMessage(message);
            System.out.println(messageHandler.readMessage());
            message = input.nextLine();
        }

    }
}




