import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;

    public ServerThread() throws IOException {
        this("QuoteServerThread");
    }

    public ServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4445);
    }

    public void run() {

        while (moreQuotes){
            try {
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // figure out response
                String message= new String(packet.getData(), 0, packet.getLength());
                System.out.println("Recieved message from a client: " + message );
                try {
                    String[] data;
                    if(message.contains("a")) {
                        data = message.split("a");
                        int sum = Integer.parseInt(data[0]) + Integer.parseInt(data[1]);
                        buf = String.valueOf(sum).getBytes(StandardCharsets.UTF_8);
                    } else if(message.contains("s")) {
                        data = message.split("s");
                        int sum = Integer.parseInt(data[0]) - Integer.parseInt(data[1]);
                        buf = String.valueOf(sum).getBytes(StandardCharsets.UTF_8);
                    } else {
                        throw new IllegalArgumentException("Invalid input from client.");
                    }
                } catch (NumberFormatException e) {
                    buf = ("Invalid input from client. Could not parse the numbers").getBytes(StandardCharsets.UTF_8);
                } catch(IllegalArgumentException e) {
                    buf = (e.getMessage() + " Could not parse operation").getBytes(StandardCharsets.UTF_8);
                }

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        socket.close();
    }
}