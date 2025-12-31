package cc.rainyctl.rainylangchain4j.controller;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.image.ImageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/chat")
public class MultimodalController {

    private final ChatModel chatModel;

    private final ImageModel imageModel;

    public MultimodalController(@Qualifier("openAI") ChatModel chatModel, ImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    @GetMapping("/img/see")
    public String see() {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("你看到了什么"),
                ImageContent.from("https://cdn.pixabay.com/photo/2025/09/12/16/49/dog-9830831_1280.jpg")
        );

        ChatResponse res = chatModel.chat(userMessage);
        return res.aiMessage().text();
    }

//    This doesn't work, only generate text.
//    @GetMapping("/img/gen")
//    public String gen() {
//        ChatResponse chat = chatModel.chat(UserMessage.from("生成一张写实的咖啡的图片"));
//        log.info("img gen res: {}", chat);
//        return chat.aiMessage().text();
//    }


    @GetMapping("/img/gen")
    public URI gen() {
        var res = imageModel.generate("a cat making coffee for me");
        log.info("generate: {}", res);
        return res.content().url();
    }
}
