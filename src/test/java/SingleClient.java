
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;

public class SingleClient {
    private static final String FREUD = "128.83.120.232";
    private static final int TEST_PORT = 3000;

    @Test
    public void test_msg_correct_single_client() {
        Client client = new Client();
        try {
            client.startConnection(FREUD, 3000);
            String resp1 = client.sendMessage("Hello");
            String resp2 = client.sendMessage("World");
            String terminate = client.sendMessage("quit");
            Assertions.assertEquals(resp1, "I got your message: Hello");
            Assertions.assertEquals(resp2, "I got your message: World");
            Assertions.assertEquals(terminate, "Exiting");
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }
}
