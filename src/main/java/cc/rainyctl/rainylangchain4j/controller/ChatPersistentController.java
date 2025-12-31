package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.service.ChatPersistentAssistant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatPersistentController {

    // redis store
    private final ChatPersistentAssistant assistant;

    // mysql store
    private final ChatPersistentAssistant assistant2;

    public ChatPersistentController(ChatPersistentAssistant assistant, @Qualifier("ps2") ChatPersistentAssistant assistant2) {
        this.assistant = assistant;
        this.assistant2 = assistant2;
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

    @GetMapping("/ps2/hello")
    public String hello3(@RequestParam(value = "id", defaultValue = "1") String userId) {
        String memoryId = "user:" + userId; // user:1
        var res1 = assistant2.chat(memoryId, "你好我的名字是Faker" + userId);
        var res2 = assistant2.chat(memoryId, "我的名字是什么？");
        return res1 + ";" +  res2;
    }

    @GetMapping("/ps2/hello2")
    public String hello4(@RequestParam(value = "id", defaultValue = "1") String userId) {
        String memoryId = "user:" + userId; // user:1
        return assistant2.chat(memoryId, "我的名字是什么？");
    }
}
