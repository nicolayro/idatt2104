import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles I/O between sockets
 */
public class MessageHandler {
    private final BufferedReader reader;
    private final PrintWriter writer;

    /**
     * Creates I/O Streams between the current socket and the socket given in the constructor
     * @param connection socket to connect with
     * @throws IOException if there is an error
     */
    public MessageHandler(Socket connection) throws IOException {
        // Connecting the client with the server
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        writer = new PrintWriter(connection.getOutputStream(), true);
    }

    /**
     * Reads a message given from the socket, and prints it to the terminal
     * Prints an error message if there is an error
     */
    public String readMessage() {
        try {
            return reader.readLine();
//            String message = reader.readLine();
//            completedMessage += message;
//            while(message != null) {
//                System.out.println(message);
//                message = reader.readLine();
//                completedMessage += message;
//            }
//            return completedMessage;
        } catch(Exception e) {
            System.out.println("Was not able to read message");
            return "There was an error";
        }
    }

    /**
     * Send a message to the given socket
     * Prints an error if there is an error
     */
    public void sendMessage(String message) {
        try {
            writer.println(message);
        } catch (Exception e) {
            System.out.println("Was not able to send message!");
        }
    }

    /**
     * Closes I/O streams. This instance is of the object is no longer usable after this call
     */
    public void close() {
        try {
            reader.close();
            writer.close();
        } catch (Exception e) {
            System.out.println("There was an error closing the I/O streams");
        }
    }
}
