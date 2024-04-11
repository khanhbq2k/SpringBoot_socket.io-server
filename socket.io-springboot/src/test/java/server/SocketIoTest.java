//package io.socket.socketio.server;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.core.env.Environment;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public final class SocketIoTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private Environment env;
//
//    @Test
//    public void test_connect() throws Exception {
//        // Ensure the server is up and running
//        // Adjust the path to the Node.js executable and the script as necessary
//        ProcessBuilder processBuilder = new ProcessBuilder(
//                "node", "src/test/resources/test_connect.js"
//        ).inheritIO();
//
//        // Pass the server port as an environment variable to the script
//        Map<String, String> environment = processBuilder.environment();
//        environment.put("PORT", String.valueOf(port));
//
//        Process process = processBuilder.start();
//        int exitCode = process.waitFor(); // Wait for the script to execute
//
//        assertEquals(0, exitCode, "The client did not connect successfully.");
//    }
//}
