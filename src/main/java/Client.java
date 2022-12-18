/**
 * Author: Allen Jue
 * Date: 12/17/2022
 */

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * An instance of a client that has the ability to connect to a server given an IP address and a port.
 */
public class Client {
    private Socket clientSocket;           /* Socket on the client side */
    private PrintWriter clientOutput;      /* Synchronous writer from the client */
    private BufferedReader clientInput;    /* Synchronous reader for the client */

    /**
     * Establishes a connection for a client
     * @param ip_addr target IP address for a client connection
     * @param port number to identify a process
     */
    public void startConnection(String ip_addr, int port)  {
        try {
            clientSocket = new Socket(ip_addr, port);
            clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Network error");
        }
    }

    /**
     * Sends a string to the server and blocks until the server repsonds with confirmation
     * @param message to be sent to server
     * @return "Message received: <message>" if the message was valid
     *         "Exiting" if the message was 'quit'
     *         null if there was a network failure
     */
    public String sendMessage(String message) {
        clientOutput.println(message);
        try {
            return clientInput.readLine();
        } catch (Exception e) {
            System.out.println("Network error: Could not read input");
            return null;
        }
    }

    /**
     * Closes a client connection.
     */
    public void stopConnection() {
        try {
            clientInput.close();
            clientOutput.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println("Failed to close connection");
        }

    }
}
