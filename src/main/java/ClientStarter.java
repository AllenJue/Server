import java.util.Scanner;

public class ClientStarter {
    private static final String FREUD = "128.83.120.232";   /* Default IP Address, lab machine */
    private static final int TEST_PORT = 3000;              /* Arbitrary port used */

    public static void main(String[] args) {
        Client client = new Client();
        client.startConnection(FREUD, TEST_PORT);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username: ");
        String username = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();
        if(username.length() > 100 || password.length() > 100) {
            System.out.println("Stack smashing bad!");
            return;
        }
        String credentialResp = client.sendMessage(username + " " + password);
        System.out.println(credentialResp);
        while(true) {
            String resp = client.sendMessage(sc.nextLine());
            System.out.println(resp);
        }
    }
}
