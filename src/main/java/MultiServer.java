/**
 * Author: Allen Jue
 * Date: 12/17/2022
 */

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
        System.out.println("Server loaded with: ");
        System.out.println(info);
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
        String encryptedPassword;

        public UserInfo(String username, String salt, String password) {
            this.username = username;
            this.salt = salt;
            this.encryptedPassword = password;
        }

        @Override
        public String toString() {
            return salt + " " + username + " " + encryptedPassword + "\n";
        }
    }
    /**
     * An abstraction of a server connection created by a server thread. A connection is given a thread,
     * and returns the connection socket created when the listening socket accepts. The server thread
     * only has a single purpose, give the client confirmation that a message was received and exit.
     */
    private class ServerConnection extends Thread {
        private final int DERIVED_KEY_LENGTH = 512;

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
                             valid = createAccount(inputCreds);
                             serverOutput.println("Account created");
                             break;
                    //     case "Login":
                    //         login(inputCreds);
                    //         break;
                    //     default:
                    //         System.out.println("Invalid input");
                    //         break;
                     }
                }
                // do some authentication with DB
                // valid = authenticate(username, password)
                // TODO replace

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

        public boolean createAccount(String[] inputCreds)
                throws NoSuchAlgorithmException, InvalidKeySpecException {
            /* Fail if invalid input (missing username or password) or username taken */
            if(inputCreds.length != 3 && !info.containsKey(inputCreds[1])) {
                return false;
            }
            String salt = getNewSalt();
            String username = inputCreds[1];
            String password = inputCreds[2];
            String encryptedPassword = getEncryptedPassword(salt, password);
            /* Add to simulated 'database' and write-through */
            info.put(username, new UserInfo(username, salt, encryptedPassword));
            return true;
        }

        private void addDataToFile(UserInfo user) throws IOException {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("DataStore.txt"));
            bufferedWriter.write(user.toString());
        }

        private String getEncryptedPassword(String salt, String password)
                throws InvalidKeySpecException, NoSuchAlgorithmException {
            /* Specify the algorithm for encryption. PBKDF2--WithHmac--SHA512
                PBKDF2 handles Password-based-key-derivative function - creates a cipher function
                WithHmac - Keyed-Hash Message Authentication Code - creates a message authentication code
                SHA512 - hash function
             */
            String algorithm = "PBKDF2WithHmacSHA1";
            final int iterations = 20000;
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            System.out.println(saltBytes.toString());
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterations, DERIVED_KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            byte[] encodedBytes = keyFactory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(encodedBytes);
        }

        /**
         * Gets base64 encoded salt.
         * @return
         */
        private String getNewSalt() throws NoSuchAlgorithmException {
            /* Use SecureRandom to get crpytographically strong random numbers */
            SecureRandom random = new SecureRandom().getInstance("SHA1PRNG");
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        }
    }
}
