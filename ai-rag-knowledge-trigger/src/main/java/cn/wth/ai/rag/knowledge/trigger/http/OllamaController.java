package cn.wth.ai.rag.knowledge.trigger.http;

/**
 * @Author: 5th
 * @Description: AI模型接口实现
 * @CreateTime: 2025-04-04 22:01
 */

import cn.wth.ai.rag.knowledge.api.IAiService;
import jakarta.annotation.Resource;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/ollama/")
public class OllamaController implements IAiService {

    @Resource
    private OllamaChatModel ollamaChatModel;

    /**
     * http://localhost:8090/api/v1/ollama/generate?model=deepseek-r1:1.5b&message=1+1
     *
     * 同步请求，通过call()方法返回完整ChatResponse对象（一次性返回全部结果）
     * 同步方法适合：快速计算、简单问答、需要完整结果的场景（如数学计算1+1）
     * 同步方法使用传统Servlet模型（阻塞式IO）
     * 响应头Content-Type: application/json
     */
    @GetMapping(value = "generate")
    @Override
    public ChatResponse generate(@RequestParam String model, @RequestParam String message) {
        return ollamaChatModel.call(new Prompt(message, OllamaOptions.builder().model(model).build()));
    }

    /**
     * http://localhost:8090/api/v1/ollama/generate_stream?model=deepseek-r1:1.5b&message=hi
     *
     * 流式响应，通过stream()返回Flux<ChatResponse>（持续推送分块结果）
     * 流式方法适合：内容生成、长文本输出、需要实时逐字/逐句展示的场景（如对话hi）
     * 流式方法基于Reactive Streams响应式流（非阻塞式IO），可配合SSE（Server-Sent Events）实现前端实时渲染
     * 响应头Content-Type: text/event-stream
     */
    @GetMapping(value = "generate_stream")
    @Override
    public Flux<ChatResponse> generateStream(@RequestParam String model, @RequestParam String message) {
        return ollamaChatModel.stream(new Prompt(message, OllamaOptions.builder().model(model).build()));
    }

}