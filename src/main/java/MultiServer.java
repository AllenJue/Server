/**
 * Author: Allen Jue
 * Date: 12/17/2022
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.prefs.Preferences;

/**
 * A server that can handle multiple client connections
 */
public class MultiServer {
    public final int SIZE_OF_INFO = 3;
    Map<String, UserInfo> info = new HashMap<>(); /* Simulates database */
    private ServerSocket serverSocket;           /* The listening socket for a server */
    public void start(int port) throws IOException {
        loadInfo();
        /* listening socket */
        serverSocket = new ServerSocket(port);
        /* continue to listen for new connections to accept */
        while(true) {
            /* Note: thread.start() differs from thread.run(), as start() creates a thread and then runs */
            new ServerConnection(serverSocket.accept()).start();
        }
    }

    private void loadInfo() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("DataStore.txt"));
        String input;
        while((input = bufferedReader.readLine()) != null) {
            String[] data = input.split("\\s+");
            if(data.length != SIZE_OF_INFO) {
                throw new InvalidObjectException("Login credentials invalid");
            }
            info.put(data[0], new UserInfo(data[0], data[1], data[2]));
        }
    }

    /**
     * Ends the server
     * @throws IOException IO failure during read or write
     */
    public void stop() throws IOException {
        serverSocket.close();
    }

    private class UserInfo {
        String username;
        String salt;
        String hashed;

        public UserInfo(String username, String salt, String password) {
            this.username = username;
            this.salt = salt;
            this.hashed = password;
        }
    }
    /**
     * An abstraction of a server connection created by a server thread. A connection is given a thread,
     * and returns the connection socket created when the listening socket accepts. The server thread
     * only has a single purpose, give the client confirmation that a message was received and exit.
     */
    private class ServerConnection extends Thread {
        private BufferedReader serverInput; /* Synchronous read for incoming data */
        private PrintWriter serverOutput;   /* Synchronous write for outgoing data */
        private Socket connFd;              /* Connection socket for communicating with client */

        /**
         * Creates a client-server connection between the user fd and connFd
         * @param connFd connection file descriptor (serverside socket) to communicate with
         * @throws IOException Error reading or writing in a network
         */
        public ServerConnection(Socket connFd) throws IOException {
            this.connFd = connFd;
            this.serverOutput = new PrintWriter(connFd.getOutputStream(), true);
            this.serverInput = new BufferedReader(new InputStreamReader(connFd.getInputStream()));
        }

        /**
         * Overrides thread method, which is called when a thread begins its thread of execution.
         * Listens for input constantly, blocking while waiting for I/O to come in.
         */
        public void run() {
            String input;
            try {
                boolean valid = false;
                String[] inputCreds = serverInput.readLine().split("\\s+");
                if(instructionNotEmpty(inputCreds.length)) {
                    switch (inputCreds[0]) {
                        case "Create":
                            createAccount(inputCreds);
                            break;
                        case "Login":
                            login(inputCreds);
                            break;
                        default:
                            System.out.println("Invalid input");
                            break;
                    }

                }
                // do some authentication with DB
                // valid = authenticate(username, password)
                // TODO replace
                valid = true;
                while(valid && (input = serverInput.readLine()) != null) {
                    if(input.equals("quit")) {
                        serverOutput.println("Exiting");
                        break;
                    }
                    serverOutput.println("Message received: " + input);
                }
                /* Done listening, release resources on server side */
                serverInput.close();
                serverOutput.close();
                connFd.close();
            } catch (Exception e) {
                System.out.println("Server error. Oopsies");
            }
        }

        public boolean instructionNotEmpty(int numInstructions) {
            return numInstructions > 1;
        }

        public boolean login(String[] inputCreds) {
            boolean success = false;
            return success;
        }
        public boolean createAccount(String[] inputCreds) {
            boolean success = false;
            if(inputCreds.length != 3) {
                return success;
            }
            String username = inputCreds[1];
            String password = inputCreds[2];
            return success;
        }
    }
}
