import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        // create socket on local with some random port number
        Socket client_socket = new Socket("127.0.0.1", 1000);
        // gets input from server
        Scanner client_input = new Scanner(client_socket.getInputStream());
        System.out.println("Enter an integer: ");
        int number = input.nextInt();
        // prints output to server
        PrintStream client_output = new PrintStream(client_socket.getOutputStream());
        client_output.println(number);
        System.out.println("Server result: " + client_input.nextInt());

    }
}
