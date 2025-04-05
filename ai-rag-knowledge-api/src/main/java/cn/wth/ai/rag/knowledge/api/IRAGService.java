package cn.wth.ai.rag.knowledge.api;

import cn.wth.ai.rag.knowledge.api.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: 5th
 * @Description:
 * @CreateTime: 2025-04-05 18:38
 */
public interface IRAGService {

    Response<List<String>> queryRagTagList();

    Response<String> uploadFile(String ragTag, List<MultipartFile> files);

}
