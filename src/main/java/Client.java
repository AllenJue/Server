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
            String resp = clientInput.readLine();
            if(resp.equals("Exiting")) {
                stopConnection();
            }
            return resp;

        } catch (Exception e) {
            System.out.println("Network error: Could not read input");
            return null;
        }
    }


    public boolean authenticate(String credentials) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username: ");
        String username = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();
        if(username.length() > 100 || password.length() > 100) {
            System.out.println("Stack smashing bad!");
            return false;
        }
        String credentialResp = this.sendMessage(username + " " + password);
        boolean success = credentialResp.equals("Exiting");
        if(!success) {
            stopConnection();
        }
        sc.close();
        System.out.println(credentialResp);
        return success;
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
