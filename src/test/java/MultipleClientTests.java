import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultipleClientTests {
    private static final String FREUD = "128.83.120.232";
    private static final int TEST_PORT = 3000;

    @Test
    public void test_open_multiple_clients() {
        Client client1 = new Client();
        Client client2 = new Client();
        try {
            client1.startConnection(FREUD, TEST_PORT);
            client2.startConnection(FREUD, TEST_PORT);
            String resp1 = client1.sendMessage("Hello");
            String terminate = client1.sendMessage("quit");
            Assertions.assertEquals(resp1, "Message received: Hello");
            Assertions.assertEquals(terminate, "Exiting");
            String resp2 = client1.sendMessage("Hello from thread 2");
            String terminate = client2.sendMessage("quit");
            Assertions.assertEquals(resp2, "Message received");
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }

}
