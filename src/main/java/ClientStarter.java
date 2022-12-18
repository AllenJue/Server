import java.util.Scanner;

public class ClientStarter {
    private static final String FREUD = "128.83.120.232";   /* Default IP Address, lab machine */
    private static final int TEST_PORT = 3000;              /* Arbitrary port used */

    public static void main(String[] args) {
        Client client = new Client();
        client.startConnection(FREUD, TEST_PORT);
        String username = "allen";
        String password = "jue";
        client.authenticate(username + " " + password);
        Scanner sc = new Scanner(System.in);
        while(true) {
            String resp = client.sendMessage(sc.nextLine());
            if(resp.equals("Exiting")) {
                break;
            }
            System.out.println(resp);
        }
        sc.close();
    }
}
