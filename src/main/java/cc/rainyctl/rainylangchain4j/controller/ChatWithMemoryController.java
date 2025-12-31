package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.service.AssistantWithMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chat/mem")
public class ChatWithMemoryController {

    AssistantWithMemory assistant;

    public ChatWithMemoryController(
//            @Qualifier("mem2")
            AssistantWithMemory assistant) {
        this.assistant = assistant;
    }


    @GetMapping("/hello")
    public String hello() {
        var msg1 = assistant.chat(1, "hello, my name is Faker");
        var msg2 = assistant.chat(2, "hello, my name is Oner");
        var msg3 = assistant.chat(1, "what's my name?");
        var msg4 = assistant.chat(2, "what's my name?");

        return String.format("[user:1]\n%s\n%s\n\n[user:2]\n%s\n%s", msg1, msg3, msg2, msg4);
    }
}
