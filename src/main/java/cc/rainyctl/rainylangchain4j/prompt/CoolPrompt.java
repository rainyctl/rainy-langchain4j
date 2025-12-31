package cc.rainyctl.rainylangchain4j.prompt;

import dev.langchain4j.model.input.structured.StructuredPrompt;
import lombok.Data;

@Data
@StructuredPrompt("以以下语气 {{tone}} 解答以下问题: {{question}}")
public class CoolPrompt {
    private String tone;
    private String question;
}
