package task1;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }


        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // Request numbers
        String message = requestCalculation();

        // Populating request
        byte[] buf = new byte[256];
        buf = message.getBytes(StandardCharsets.UTF_8);

        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);

        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Answer: " + received);

        socket.close();
    }

    public static String requestCalculation() {
        // Set up scanner
        Scanner input = new Scanner(System.in);

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

                // If all input is fine, we will get here, and we can safely send the message

                // Handling response
                input.close();
                return a + operator + b;
            } catch(InputMismatchException e) {
                System.out.println("Sorry, that is not a valid number. Try again.");
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage() + " is not a valid operation. Please enter 'a' or 's' next time");
            }
        }
    }
}
