import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.sound.sampled.SourceDataLine;

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
        while(true) {
            try {
                //Input
                System.out.println("Please enter a number");
                int a = input.nextInt();
                System.out.println("Please enter another number");
                int b = input.nextInt();
                System.out.println("Thank you! Now, do you want to add or subtract them? (a/s)");
                String operator = input.next();
                if(!operator.equals("a") && !operator.equals("s")) throw new IllegalArgumentException(operator);

                // If all input is fine, we will get here, and we can safely send the message;
                messageHandler.sendMessage(a + operator + b);

                // Handling response
                String message = messageHandler.readMessage();
                if(message == null) {
                    break;
                }
                System.out.println(message);
            } catch(InputMismatchException e) {
                System.out.println("Sorry, that is not a valid number. Try again.");
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage() + " is not a valid operation. Please enter 'a' or 's' next time");
            }
        }
    }
}




