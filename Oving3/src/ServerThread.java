import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread{
    private final Socket socket;
    private MessageHandler messageHandler;

    static final String HTML_START =
        "<!DOCTYPE html>" + 
        "<html>" +
        "<title>My first HTTP Server</title>" +
        "<meta charset=\"UTF-8\">" + 
        "<body>";

    static final String HTML_END =
        "</body>" +
        "</html>";
    
    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            messageHandler = new MessageHandler(socket);
            handleConnection();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void handleConnection() {
        System.out.println("A client connected to the server!");
        String message = messageHandler.readMessage();
        while(message != null) {
            if(message.contains(("GET /"))) {
                handleWebConnection(message);
                return;
            }
            if(message.equals("There was an error")){
                messageHandler.close();
                System.out.println("Closing connection...");
                return;
            } 
            System.out.println("Recieved message from a client: " + message);
            try {
                String[] data;
                if(message.contains("a")) {
                    data = message.split("a");  
                    int sum = Integer.parseInt(data[0]) + Integer.parseInt(data[1]);
                    messageHandler.sendMessage(Integer.toString(sum));
                } else if(message.contains("s")) {
                    data = message.split("s");
                    int sum = Integer.parseInt(data[0]) - Integer.parseInt(data[1]);
                    messageHandler.sendMessage(Integer.toString(sum));
                } else {
                    throw new IllegalArgumentException("Invalid input from client.");
                }
            } catch (NumberFormatException e) {
                messageHandler.sendMessage("Invalid input from client. Could not parse the numbers");
            } catch(IllegalArgumentException e) {
                messageHandler.sendMessage(e.getMessage() + " Could not parse operation");
            }                 
            message = messageHandler.readMessage();
        }
    }

    private void handleWebConnection(String message) {
        System.out.println("Connected to a web client!");
        messageHandler.sendMessage("HTTP/1.1 200 OK\r\n");
        messageHandler.sendMessage(HTML_START);
        messageHandler.sendMessage("<h1>Velkommen til nettsiden min! Her er headeren til forespørselen din:</h1>");
        messageHandler.sendMessage("<ul>");
        while(message != null) {
            if(message.equals("There was an error") || message.isEmpty()) break;
            messageHandler.sendMessage("<li>" + message + "</li>");
            message = messageHandler.readMessage();
        }
        messageHandler.sendMessage("</ul>");
        messageHandler.sendMessage(HTML_END);
        System.out.println("HTML SENT");
        messageHandler.close();
   }
}
