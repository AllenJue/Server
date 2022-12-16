import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        // listening socket
        ServerSocket serverSocket = new ServerSocket(3000);
        // create a connection socket
        Socket connFd = serverSocket.accept();
        Scanner server_input = new Scanner(connFd.getInputStream());
        PrintStream server_output = new PrintStream(connFd.getOutputStream());
        while(true) {
            int number = server_input.nextInt();
            int temp = number * 2;
            server_output.println(temp);
        }

    }
}
