package com.example.loveagent.invoke;

import java.util.Arrays;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.example.loveagent.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SdkAiInvoke {
    
    @Autowired
    private ApiConfig apiConfig;

    public GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        if (!apiConfig.isApiKeyConfigured()) {
            throw new RuntimeException("API密钥未配置，请在application-local.yml中设置dashscope.api.key或设置环境变量DASHSCOPE_API_KEY");
        }
        
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("你是谁？")
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiConfig.getApiKey())
                .model(apiConfig.getDefaultModel())
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }
}
