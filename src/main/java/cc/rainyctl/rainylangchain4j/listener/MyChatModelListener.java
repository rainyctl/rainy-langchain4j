package cc.rainyctl.rainylangchain4j.listener;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class MyChatModelListener implements ChatModelListener {
    private static final String TRACE_ID = "TRACE_ID";
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        String id = UUID.randomUUID().toString();
        log.info("onRequest: {}", id);
        requestContext.attributes().put(TRACE_ID, id);
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        Object id =  responseContext.attributes().get(TRACE_ID);
        log.info("onResponse: {}", id);
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Object id =  errorContext.attributes().get(TRACE_ID);
        log.info("onError: {}", id);
    }
}
