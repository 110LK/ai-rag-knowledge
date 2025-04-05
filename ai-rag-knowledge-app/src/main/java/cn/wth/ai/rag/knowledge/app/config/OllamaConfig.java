package cn.wth.ai.rag.knowledge.app.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class OllamaConfig {

    @Bean
    public OllamaApi ollamaApi(@Value("${spring.ai.ollama.base-url}") String baseUrl) {
        return new OllamaApi(baseUrl);
    }

    @Bean
    public OllamaChatModel chatClient(OllamaApi ollamaApi) {
        return OllamaChatModel.builder().ollamaApi(ollamaApi).build();
    }

    /**
     * 配置文本分割器。
     * 这个Bean负责创建文本分割工具，可以把长文本切分成小段落。
     * 在做文档检索时，通常需要先把文档分成小块再处理，这个分割器就是干这个的。
     */
    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

    /**
     * 配置内存向量存储。
     *
     * 这个Bean创建一个内存中的向量数据库，用来存储和检索文本的向量表示。
     * 它使用Ollama的API将文本转换为向量，并用nomic-embed-text模型生成这些向量。
     * 适合小规模应用或测试使用。
     *
     * @param ollamaApi AI模型服务的接口
     * @return 返回一个可以在内存中存储向量的数据库
     */
    @Bean
    public SimpleVectorStore simpleVectorStore(OllamaApi ollamaApi) {
        OllamaOptions options = new OllamaOptions();
        options.setModel("nomic-embed-text");
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        ModelManagementOptions managementOptions = ModelManagementOptions.defaults();
        var embeddingModel = new OllamaEmbeddingModel(ollamaApi, options, observationRegistry, managementOptions);
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * 配置PostgreSQL向量存储。
     * 这个Bean创建一个基于PostgreSQL的向量数据库，适合存储大量数据和生产环境使用。
     * 它同样使用Ollama的API生成向量，但会把这些向量保存在PostgreSQL数据库中。
     * 使用前需要确保PostgreSQL已安装vector扩展。
     *
     * @param ollamaApi AI模型服务的接口
     * @param jdbcTemplate 数据库连接工具
     * @return 返回一个可以在PostgreSQL中存储向量的数据库
     */
    @Bean
    public PgVectorStore pgVectorStore(OllamaApi ollamaApi, JdbcTemplate jdbcTemplate) {
        OllamaOptions options = new OllamaOptions();
        options.setModel("nomic-embed-text");
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        ModelManagementOptions managementOptions = ModelManagementOptions.defaults();
        var embeddingModel = new OllamaEmbeddingModel(ollamaApi, options, observationRegistry, managementOptions);
        return PgVectorStore.builder(jdbcTemplate, embeddingModel).build();
    }

}