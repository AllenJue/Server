import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        // listening socket
        ServerSocket serverSocket = new ServerSocket(1000);
        // create a connection socket
        Socket connFd = serverSocket.accept();
        Scanner server_input = new Scanner(connFd.getInputStream());
        int number = server_input.nextInt();
        int temp = number * 2;
        PrintStream server_output = new PrintStream(connFd.getOutputStream());
        server_output.println(temp);
    }
}
