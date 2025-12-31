package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.service.ChatPersistentAssistant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatPersistentController {

    private final ChatPersistentAssistant assistant;

    public ChatPersistentController(ChatPersistentAssistant assistant) {
        this.assistant = assistant;
    }

    @GetMapping("/ps/hello")
    public String hello(@RequestParam(value = "id", defaultValue = "1") String userId) {
        String memoryId = "user:" + userId; // user:1
        var res1 = assistant.chat(memoryId, "你好我的名字是Faker" + userId);
        var res2 = assistant.chat(memoryId, "我的名字是什么？");
        return res1 + ";" +  res2;
    }

    @GetMapping("/ps/hello2")
    public String hello2(@RequestParam(value = "id", defaultValue = "1") String userId) {
        String memoryId = "user:" + userId; // user:1
        return assistant.chat(memoryId, "我的名字是什么？");
    }
}
