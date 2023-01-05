/**
 * Author: Allen Jue
 * Date: 01/04/2023
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

/**
 * A subclass of MultiServer, which holds the same functions.
 * Uses an AWS RDS to hold Credentials instead of a local datastore.
 */
public class DatabaseMultiServer extends MultiServer {
    private Connection connection;               /* Connection with database */

    /**
     * Initializes a server by loading in data from a database and listens to connection requests
     * @param port to identify server process by
     * @exception SQLException if database had an error
     * @exception IOException if listening connection could not be made
     */
    public void start(int port) throws SQLException, IOException {
        System.out.println("Connecting to database.");
        String connectionUrl = "jdbc:mysql://YOUR_ENDPOINT:3306";
        String username = "YOUR_USERNAME";
        String password = "YOUR_PASSWORD";
        connection = DriverManager.getConnection(connectionUrl, username, password);
        /* Ensures that the table is of credentials exists in the database */
        establishDatabase();
        serverSocket = new ServerSocket(port);
        while(true) {
            /* Note: thread.start() differs from thread.run(), as start() creates a thread and then runs */
            new ServerConnection(serverSocket.accept()).start();
        }
    }

    /**
     * Establishes a database and connection to the database
     * @throws SQLException if database could not be connected to
     */
    private void establishDatabase() throws SQLException {
        /* Try creating database */
        try {
            Statement createeDatabaseStatement = connection.createStatement();
            createeDatabaseStatement.executeUpdate("create database MyDatabase");
            System.out.println("MyDatabase created.");
        } catch (Exception e) {
            System.out.println("Database already created");
        }
        /* Connect to database */
        Statement useDatabaseStatement = connection.createStatement();
        useDatabaseStatement.executeUpdate("use MyDatabase");
        System.out.println("Using MyDatabase.");
        /* Try creating a Credential table */
        try {
            Statement createCredentialsTableStatement = connection.createStatement();
            createCredentialsTableStatement.executeUpdate("create table Credentials(salt varchar(12), " +
                    "username varchar(16), password varchar(28), primary key(username))");
            System.out.println("Password table created.");
        } catch (Exception e) {
            System.out.println("Table already exists");
        }
    }

    /**
     * Database version of a ServerConnection. To log in a user, need to consult
     * database of user credentials.
     */
    private class ServerConnection extends MultiServer.ServerConnection {

        /**
         * Creates a client-server connection between the user fd and connFd
         *
         * @param connFd connection file descriptor (serverside socket) to communicate with
         * @throws IOException Error reading or writing in a network
         */
        public ServerConnection(Socket connFd) throws IOException {
            super(connFd);
        }

        /**
         * Attempts to log in a user
         * @param inputCreds login credentials
         * @return true if log in successful
         */
        public boolean login(String[] inputCreds) {
            /* Missing username or password */
            if(inputCreds.length != SIZE_OF_INFO) {
                return false;
            }
            try {
                String username = inputCreds[1];
                String password = inputCreds[2];
                /* Gets a query for a rows with specified username */
                ResultSet result = findUsername(username);
                /* next moves pointer forwards to the first element in the query */
                if(!result.next()) {
                    return false;
                }
                String salt = result.getString("salt");
                String hashedPassword = result.getString("password");
                /* Returns true if passwords match */
                return getEncryptedPassword(salt, password).equals(hashedPassword);
            } catch (Exception e) {
                System.out.println(e);
            }
            return false;
        }

        /**
         * Creates a user account
         * @param inputCreds credentials of user
         * @return true if account creation was successful
         */
        public boolean createAccount(String[] inputCreds) {
            /* Missing username or password, return false */
            if(inputCreds.length != SIZE_OF_INFO) {
                return false;
            }
            try {
                String username = inputCreds[1];
                String password = inputCreds[2];
                /* Uses query to find if username is taken */
                ResultSet result = findUsername(username);
                /* Moves pointer forwards to the first element in the query (true if username taken) */
                if(result.next()) {
                    System.out.println("Username taken");
                    return false;
                }
                /* Generates a new salt and password for user */
                String salt = getNewSalt();
                String hashedPassword = getEncryptedPassword(salt, password);
                /* Use prepared statement to prevent SQL injections */
                PreparedStatement preparedStatement =
                        connection.prepareStatement(
                                "INSERT INTO Credentials (salt, username, password) VALUES (?, ?, ?)");
                preparedStatement.setString(1, salt);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, hashedPassword);
                /* Add row with new salt, username, and hashed password */
                preparedStatement.executeUpdate();
                return true;
            } catch (Exception e) {
                System.out.println(e);
            }
            return false;
        }

        /**
         * Queries the Credential table for a row with 'username'
         * @param username key to search for in table
         * @return ResultSet - a query of row(s) with a specified 'username'
         */
        private ResultSet findUsername(String username) {
            try {
                /* Use a PreparedStatement to prevent SQL injection when querying */
                PreparedStatement preparedStatement =
                        connection.prepareStatement("SELECT * FROM Credentials WHERE username =?");
                preparedStatement.setString(1, username);
                return preparedStatement.executeQuery();
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        }
    }
}
