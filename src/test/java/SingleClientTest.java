
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleClientTest {
    private static final String FREUD = "128.83.120.232";
    private static final int TEST_PORT = 3000;

    @Test
    public void test_msg_correct_single_client() {
        Client client = new Client();
        try {
            client.startConnection(FREUD, TEST_PORT);
            String resp1 = client.sendMessage("Hello");
            String resp2 = client.sendMessage("World");
            String terminate = client.sendMessage("quit");
            Assertions.assertEquals(resp1, "Message received: Hello");
            Assertions.assertEquals(resp2, "Message received: World");
            Assertions.assertEquals(terminate, "Exiting");
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }

    @Test
    public void test_msg_disconnect_and_reconnect_client() {
        Client client = new Client();
        try {
            client.startConnection(FREUD, TEST_PORT);
            String resp1 = client.sendMessage("Connecting");
            String terminate = client.sendMessage("quit");
            Assertions.assertEquals(resp1, "Message received: Connecting");
            Assertions.assertEquals(terminate, "Exiting");
            client.startConnection(FREUD, TEST_PORT);
            String resp2 = client.sendMessage("Reconnecting");
            String terminate2 = client.sendMessage("quit");
            Assertions.assertEquals(resp2, "Message received: Reconnecting");
            Assertions.assertEquals(terminate, "Exiting");
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }
}
