//package server;
//
//import io.socket.engineio.server.EngineIoServer;
//import com.khanhbq.socket.SpringWebSocketWrapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = SpringWebSocketWrapper.class)
//public class HttpEndpointTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private EngineIoServer engineIoServer;
//
//    @Test
//    public void testHttpRequestHandling() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/chat/ws")
//                        .param("subject", "testSubject"))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//}
//
