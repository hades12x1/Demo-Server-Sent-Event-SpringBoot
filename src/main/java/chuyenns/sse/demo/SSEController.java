package chuyenns.sse.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/sse")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class SSEController {
    private final ObjectMapper MAPPER = new ObjectMapper();
    public final long MAX_TIMEOUT_SSE_MILLISECOND = 30000L;
    public Map<String, SseEmitter> emitterMapByUserId = new HashMap<>();

    /**
     * Đăng ký user nhận event.
     * @param userId
     * @return
     */
    @RequestMapping(value = "/subscribe/{userId}", method = RequestMethod.GET)
    public SseEmitter subcribe(@PathVariable("userId") String userId) {
        try {
            // Time out của SSE là 30s, tuỳ chỉnh.
            SseEmitter emitter = new SseEmitter(MAX_TIMEOUT_SSE_MILLISECOND);
            emitter.send(SseEmitter.event().name("CONNECTION").data("Connection established"));
            emitterMapByUserId.put(userId, emitter);

            emitter.onTimeout(() -> {
                emitter.complete();
                emitterMapByUserId.remove(userId);
            });

            emitter.onError(e -> {
                e.printStackTrace();
                emitter.complete();
                emitterMapByUserId.remove(userId);
            });
            return emitter;
        } catch (Exception ex) {
            throw new RuntimeException("Error when subcribe", ex);
        }
    }

    /**
     * Gửi event đến user từ API(Chỉ gửi cho 1 user)
     * Thực hiện cho việc test, thực tế chỉ cần lấy SSEEmitter từ emitterMapByUserId và gọi emitter.send() là được.
     * @param userId
     * @param event
     */
    @PostMapping("/{userId}")
    public Object pushEventToUser(@PathVariable("userId") String userId, @RequestBody EventData event) {
        SseEmitter emitter = emitterMapByUserId.get(userId);
        event.setUserId(userId);
        if (emitter == null) {
            return "User not found";
        }

        try {
            String json = MAPPER.writeValueAsString(event);
            emitter.send(SseEmitter.event().name("MESSAGE").data(json));
        } catch (Exception ex) {
            emitterMapByUserId.remove(userId);
            return "Error when push event to user: " + userId;
        }
        return event;
    }

    /**
     * Đóng connection SSE: thành công hay không vẫn trả về message ok.
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/close")
    public String closeConnectionByUserId(@PathParam("userId") String userId) {
        SseEmitter emitter = emitterMapByUserId.get(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } finally {
                emitterMapByUserId.remove(userId);
            }
        }
        return "OK";
    }

    @GetMapping("/available")
    public Set<String> getAvailableConnection() {
        return emitterMapByUserId.keySet();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventData {
        private String userId;
        private String type;
        private String title;
        private String data;
    }
}
