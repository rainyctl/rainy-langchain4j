package cc.rainyctl.rainylangchain4j.controller;

import cc.rainyctl.rainylangchain4j.service.MCPAssistant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/mcp")
public class MCPController {

    private final MCPAssistant assistant;

    public MCPController(MCPAssistant assistant) {
        this.assistant = assistant;
    }

    // without time mcp will get something like
    // I can’t access your current location or real-time data, so I don’t know your exact time.
    // To find your current time, you can: 1. Check the clock on your device (phone, computer, tablet).
    // 2. Search online for “current time” in your city or time zone.
    // 3. Use voice commands like “Hey Google, what time is it?” or “Hey Siri, what’s the time?”
    // If you tell me your city or time zone, I can help calculate it for you! 😊
    @GetMapping("/hello")
    public String hello() {
//        return assistant.chat("what's my current time?"); // will still ask for timezone or location if not config in command line
        // return assistant.chat("what's my current time in Palo Alto?"); // ok
         return assistant.chat("what's current time in Palo Alto and China ?");
    }
}
