package cc.rainyctl.rainylangchain4j.controller;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/chat")
public class StreamChatController {
    private final StreamingChatModel model;

    public StreamChatController(StreamingChatModel model) {
        this.model = model;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public SseEmitter chat(@RequestParam(value = "message", defaultValue = "你好，你是谁")  String message) {
        SseEmitter emitter = new SseEmitter();

        model.chat(message, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String partialResponse) {
                log.info("onPartialResponse: {}", partialResponse);
                try {
                    emitter.send(partialResponse);
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                log.info("onCompleteResponse: {}", chatResponse);
                emitter.complete();
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("onError", throwable);
                emitter.completeWithError(throwable);
            }
        });

        return emitter;
    }
}
