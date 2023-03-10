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
    public void startConnection(String ip_addr, int port) throws IOException {
        clientSocket = new Socket(ip_addr, port);
        clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
        clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /**
     * Sends a string to the server and blocks until the server repsonds with confirmation
     * @param message to be sent to server
     * @return "Message received: <message>" if the message was valid
     *         "Exiting" if the message was 'quit'
     *         null if there was a network failure
     */
    public String sendMessage(String message) throws IOException {
        clientOutput.println(message);
        String resp = clientInput.readLine();
        if(resp.equals("Exiting")) {
            stopConnection();
        }
        return resp;
    }


    /**
     * Prompts user for username and password
     * @return username + " " + password
     */
    public String getCredInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username: ");
        String username = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();
        return username + " " + password;
    }

    /**
     * Authenticates user based on their credentials. Closes connection if invalid
     * @param credentials user's username concatenated by their password
     * @param create flag to designate if user wishes to create an account or login
     * @return true if user was successfully able to access the database
     * @throws IOException if unable to read input
     */
    public boolean authenticate(String credentials, boolean create) throws IOException {
        /* TODO check for stack smashing */
        String credentialResp = create ? this.sendMessage("Create " + credentials) :
                this.sendMessage("Login " + credentials);
        return !credentialResp.equals("Exiting");
    }


    /**
     * Closes a client connection.
     */
    public void stopConnection() throws IOException {
        clientInput.close();
        clientOutput.close();
        clientSocket.close();
    }
}
