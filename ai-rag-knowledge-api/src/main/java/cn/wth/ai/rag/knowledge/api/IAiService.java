package cn.wth.ai.rag.knowledge.api;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * @Author: 5th
 * @Description: AI模型接口
 * @CreateTime: 2025-04-04 21:57
 */
public interface IAiService {

    ChatResponse generate(String model, String message);

    Flux<ChatResponse> generateStream(String model, String message);

    Flux<ChatResponse> generateStreamRag(String model, String ragTag, String message);

}
