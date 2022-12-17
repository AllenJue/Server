import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;           /* Socket on the client side */
    private PrintWriter clientOutput;      /* Synchronous writer from the client */
    private BufferedReader clientInput;    /* Synchronous reader for the client */

    /**
     * Establishes a connection for a client
     * @param ip_addr target IP address for a client connection
     * @param port number to identify a process
     * @throws IOException error when trying to establish a connection
     */
    public void startConnection(String ip_addr, int port) throws IOException {
        clientSocket = new Socket(ip_addr, port);
        clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);
        clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String message) throws IOException {
        clientOutput.println(message);
        String resp = clientInput.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        clientInput.close();
        clientOutput.close();
        clientSocket.close();
    }

//    public static void main(String[] args) throws IOException {
//        Scanner input = new Scanner(System.in);
//        // create socket on freud.cs.utexas.edu with some random port number
//        client_socket = new Socket("128.83.120.232", 3000);
//        // gets input from server
//        Scanner client_input = new Scanner(client_socket.getInputStream());
//        PrintStream client_output = new PrintStream(client_socket.getOutputStream());
//        while(true) {
//            System.out.println("Enter an integer: ");
//            int number = input.nextInt();
//            // prints output to server
//            client_output.println(number);
//            System.out.println("Server result: " + client_input.nextInt());
//        }
//    }
}
