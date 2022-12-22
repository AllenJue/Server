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
    public final int SIZE_OF_INFO = 3;           /* Length of initial request */
    Map<String, UserInfo> userInformation = new HashMap<>(); /* Simulates database */
    private ServerSocket serverSocket;           /* The listening socket for a server */

    private String dataStorePath;                /* Location of dataStore */
    /**
     * Initializes a server by loading in data from a specified data store and listens to connection requests.
     * @param port to identify server process by
     * @param dataStoreName name of datastore to load user data from
     * @throws IOException if listening connection could not be made
     */
    public void start(int port, String dataStoreName) throws IOException {
        dataStorePath = dataStoreName;
        loadInfo(dataStorePath);
        /* listening socket */
        serverSocket = new ServerSocket(port);
        /* continue to listen for new connections to accept */
        while(true) {
            /* Note: thread.start() differs from thread.run(), as start() creates a thread and then runs */
            new ServerConnection(serverSocket.accept()).start();
        }
    }

    /**
     * Upon starting the Server, load in user data from a DataStore to allow for user logins
     * @param dataStoreName name of datastore to be read from
     * @throws IOException if dataStorePath does not exist
     */
    private void loadInfo(String dataStoreName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(dataStoreName));
        String input;
        while((input = bufferedReader.readLine()) != null) {
            String[] data = input.split("\\s+");
            if(data.length != SIZE_OF_INFO) {
                throw new InvalidObjectException("Login credentials invalid");
            }
            userInformation.put(data[1], new UserInfo(data[0], data[1], data[2]));
        }
        System.out.println("Server loaded with: ");
        System.out.println(userInformation);
    }

    /**
     * Ends the server
     * @throws IOException IO failure during read or write
     */
    public void stop() throws IOException {
        serverSocket.close();
    }

    /**
     * UserInfo objects hold the username, salt, and encrypted password for a user.
     */
    private class UserInfo {
        private String salt;
        private String username;
        private String encryptedPassword;

        /**
         * Creates a UserInfo object
         * @param username user's username
         * @param salt associated with user's account
         * @param password encrypted password for this account
         */
        public UserInfo(String username, String salt, String password) {
            this.username = username;
            this.salt = salt;
            this.encryptedPassword = password;
        }

        /**
         * Creates a String representation for a UserInfo object.
         * @return the fields of a UserInfo appended onto each other
          */
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
        private final int DERIVED_KEY_LENGTH = 160; /* SHA1 creates a key of 160 bits long */
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
                boolean valid = handleLoginOrCreation();
                if(!valid) {
                    serverOutput.println("Exiting");
                }
                serverOutput.println("Authentication result: " + valid);
                while(valid && (input = serverInput.readLine()) != null) {
                    if(input.equals("quit")) {
                        serverOutput.println("Exiting");
                        break;
                    }
                    serverOutput.println("Message received: " + input);
                }
                /* Done listening, release resources on server side */
                endConnection();
            } catch (Exception e) {
                System.out.println("Server error. Oopsies");
            }
        }

        /**
         * Handles a user's initial input to login or create an account
         * @return true if request was successful
         */
        private boolean handleLoginOrCreation() {
            boolean valid = false;
            try {
                String[] inputCreds = serverInput.readLine().split("\\s+");
                if (instructionNotEmpty(inputCreds.length)) {
                    switch (inputCreds[0]) {
                        case "Create":
                            valid = createAccount(inputCreds);
                            System.out.println("Account attempt creation");
                            break;
                        case "Login":
                            valid = login(inputCreds);
                            System.out.println("login attempted");
                            break;
                        default:
                            System.out.println("Invalid input");
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return valid;
        }

        /**
         * Ends a connection from the server side once completed
         * @throws IOException if input or output device could not successfully close
         */
        private void endConnection() throws IOException {
            serverInput.close();
            serverOutput.close();
            connFd.close();
        }

        /**
         * Checks if instructions have something in them along with them
         * @param numInstructions number of instructions passed in
         * @return numInstructions > 1
         */
        public boolean instructionNotEmpty(int numInstructions) {
            return numInstructions > 1;
        }

        /**
         * Logs user into their account.
         * @param inputCreds login credentials
         * @return true if user successfully logged in
         * @throws InvalidKeySpecException key spec could not be generated
         * @throws NoSuchAlgorithmException No hash algorithm found
         */
        public boolean login(String[] inputCreds) throws InvalidKeySpecException, NoSuchAlgorithmException {
            /* Invalid if no username/password or the username does not exist */
            if(inputCreds.length != SIZE_OF_INFO || !userInformation.containsKey(inputCreds[1])) {
                return false;
            }
            String username = inputCreds[1];
            String password = inputCreds[2];
            UserInfo loggedInfo = userInformation.get(username);
            String curEncryptedPassword = getEncryptedPassword(loggedInfo.salt, password);
            return curEncryptedPassword.equals(loggedInfo.encryptedPassword);
        }

        /**
         * Creates a user account by generating a salt and encrypted password for a user.
         * This is then kept in a separate datastore.
         * @param inputCreds credentials of user
         * @return true if an account was successfully created
         * @throws NoSuchAlgorithmException if PBKDF2WithHmacSHA1 is not a valid algorithm
         * @throws InvalidKeySpecException if key spec generation failed
         * @throws IOException if data could not be written to datastore
         */
        public boolean createAccount(String[] inputCreds)
                throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
            /* Fail if invalid input (missing username or password) or username taken */
            if(inputCreds.length != SIZE_OF_INFO || userInformation.containsKey(inputCreds[1])) {
                return false;
            }
            String salt = getNewSalt();
            String username = inputCreds[1];
            String password = inputCreds[2];
            String encryptedPassword = getEncryptedPassword(salt, password);
            /* Add to simulated 'database' and write-through */
            userInformation.put(username, new UserInfo(username, salt, encryptedPassword));
            addDataToFile(userInformation.get(username));

            return true;
        }

        /**
         * Write-through user data to datastore
         * @param user Object that holds user data
         * @throws IOException if dataStorePath could not be opened
         */
        private void addDataToFile(UserInfo user) throws IOException {
            /* Open file to write to and true to append */
            FileWriter writer = new FileWriter(dataStorePath, true);
            writer.write(user.toString());
            writer.close();
        }

        /**
         * Gets the encrypted password of a password and salt combination
         * @param salt specific salt for an account to prevent rainbow table attacks
         * @param password user specified password
         * @return an encrypted password
         * @throws InvalidKeySpecException if Key Spec could not be made
         * @throws NoSuchAlgorithmException if PBKDF2WithHmacSHA1 could not be resolved
         */
        private String getEncryptedPassword(String salt, String password)
                throws InvalidKeySpecException, NoSuchAlgorithmException {
            /* Specify the algorithm for encryption. PBKDF2--WithHmac--SHA1
                PBKDF2 handles Password-based-key-derivative function - creates a cipher function
                WithHmac - Keyed-Hash Message Authentication Code - creates a message authentication code
                SHA1 - hash function
             */
            String algorithm = "PBKDF2WithHmacSHA1";
            final int iterations = 20000;
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, iterations, DERIVED_KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            byte[] encodedBytes = keyFactory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(encodedBytes);
        }

        /**
         * Gets base64 encoded salt.
         * @return a cryptographically-sound salt
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
