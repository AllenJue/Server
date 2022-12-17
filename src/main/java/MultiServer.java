import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {
    private ServerSocket serverSocket;
    public void start(int port) throws IOException {
        // listening socket
        serverSocket = new ServerSocket(port);
        // create a connection socket
        while(true) {
            // Note: thread.start() differs from thread.run(),
            // as start() creates a thread and then runs
            new ServerConnection(serverSocket.accept()).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private class ServerConnection extends Thread {
        private BufferedReader serverInput;
        private PrintWriter serverOutput;
        private Socket connFd;
        public ServerConnection(Socket connFd) throws IOException {
            this.connFd = connFd;
            this.serverOutput = new PrintWriter(connFd.getOutputStream(), true);
            this.serverInput = new BufferedReader(new InputStreamReader(connFd.getInputStream()));
        }

        public void run() {
            String input;
            try {
                while((input = serverInput.readLine()) != null) {
                    if(input.equals("quit")) {
                        serverOutput.println("Exiting");
                        break;
                    }
                    serverOutput.println("Message received: " + input);
                }

                serverInput.close();
                serverOutput.close();
                connFd.close();
            } catch (Exception e) {
                System.out.println("Server error. Oopsies");
            }
        }
    }
}
