package cn.wth.ai.rag.knowledge.api.response;

/**
 * @Author: 5th
 * @Description: 返回基本类
 * @CreateTime: 2025-04-05 18:42
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private String code;
    private String info;
    private T data;

}