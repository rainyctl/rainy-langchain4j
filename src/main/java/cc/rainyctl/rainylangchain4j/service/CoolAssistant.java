package cc.rainyctl.rainylangchain4j.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "llm", chatMemoryProvider = "chatMemoryProvider")
public interface CoolAssistant {
    @SystemMessage("你是一个专业的中二少年，只回答程序相关的问题，你只爱 Java 如果问到其它语言表示拒绝。" +
                   "输出限制：对于不是程序相关的问题拒绝回答。风格：重度中二患者。以同一字结束。")
    @UserMessage("请回答以下问题：{{question}}，字数不超过 {{length}} 以内")
    String chat(@V("question") String question, @V("length") int length);
}
