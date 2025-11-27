package com.hmdp.service;



import com.alibaba.fastjson.JSON;
import com.hmdp.dto.CommentAnalysisResponse;
import com.hmdp.dto.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Primary
public class WenxinAiService {

    private final StringRedisTemplate redisTemplate;

    @Value("${wenxin.api.key:}")
    private String apiKey;


    private static final String CACHE_KEY_QA = "ai:qa:";
    private static final String CACHE_KEY_COMMENT = "ai:analysis:comment:";
    // 删除 ACCESS_TOKEN_KEY，智谱AI不需要access token

    @Autowired
    private RestTemplate restTemplate;

    public WenxinAiService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 智能问答 - 修改为智谱AI API
     */
    public String answerQuestion(String question) {
        System.out.println("🤖 智谱AI处理问题: " + question);

        // 检查是否配置了API密钥
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("⚠️ 智谱AI API密钥未配置，使用模拟回答");
            return getFallbackAnswer(question);
        }

        // 1. 缓存检查
        String cacheKey = CACHE_KEY_QA + question.hashCode();
        String cachedAnswer = redisTemplate.opsForValue().get(cacheKey);
        if (cachedAnswer != null) {
            System.out.println("✅ 从缓存返回答案");
            return cachedAnswer;
        }

        try {
            // 智谱AI API端点
            String url = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

            // 2. 构建请求 - 智谱AI格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "glm-4-flash");  // 使用轻量版，响应更快
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "你是一个校园生活服务助手，专门回答关于校园周边商铺、餐饮、娱乐场所的问题。请用友好、专业的语气回答用户问题，回答要简洁明了、实用有帮助。请直接给出回答，不要提及你是AI助手。"),
                    Map.of("role", "user", "content", question)
            ));
            requestBody.put("stream", false);
            requestBody.put("max_tokens", 1024);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);  // 智谱AI使用Bearer Token

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 3. 调用智谱AI API
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            System.out.println("🔍 智谱AI响应: " + responseBody);

            if (responseBody != null && responseBody.containsKey("choices")) {
                Map<String, Object> choice = (Map<String, Object>) ((java.util.List) responseBody.get("choices")).get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                String answer = (String) message.get("content");

                System.out.println("✅ 智谱AI返回答案: " + answer);

                // 4. 缓存结果
                redisTemplate.opsForValue().set(cacheKey, answer, 1, TimeUnit.HOURS);

                return answer;
            } else {
                System.out.println("❌ 智谱AI API返回异常: " + responseBody);
                return getFallbackAnswer(question);
            }

        } catch (Exception e) {
            System.out.println("❌ 智谱AI调用失败: " + e.getMessage());
            e.printStackTrace();
            return getFallbackAnswer(question);
        }
    }

    /**
     * 评论情感分析 - 修改为智谱AI API
     */
    public Map<String, Object> analyzeComment(Long shopId, String comment) {
        System.out.println("📊 智谱AI分析评论: " + comment);

        // 1. 缓存检查
        String cacheKey = CACHE_KEY_COMMENT + shopId + ":" + comment.hashCode();
        String cachedResult = redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return JSON.parseObject(cachedResult, Map.class);
        }

        try {
            // 智谱AI API端点
            String url = "https://open.bigmodel.cn/api/paas/v4/chat/completions";

            // 2. 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "glm-4-flash");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", buildCommentAnalysisPrompt(comment)),
                    Map.of("role", "user", "content", "请分析这条评论: " + comment)
            ));
            requestBody.put("stream", false);
            requestBody.put("max_tokens", 1024);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 3. 调用智谱AI API
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                Map<String, Object> choice = (Map<String, Object>) ((java.util.List) responseBody.get("choices")).get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                String resultText = (String) message.get("content");

                System.out.println("✅ 智谱AI返回分析结果: " + resultText);

                // 4. 解析JSON结果
                Map<String, Object> analysisResult = parseCommentAnalysisResult(resultText);

                // 5. 缓存结果
                redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(analysisResult), 24, TimeUnit.HOURS);

                return analysisResult;
            } else {
                System.out.println("❌ 智谱AI API返回异常: " + responseBody);
                return getDefaultAnalysisResult();
            }

        } catch (Exception e) {
            System.out.println("❌ 评论分析失败: " + e.getMessage());
            return getDefaultAnalysisResult();
        }
    }

    /**
     * 构建问答Prompt - 保持不变
     */
    private String buildQAPrompt(String question) {
        return """
            你是一个校园生活服务助手，专门回答关于校园周边商铺、餐饮、娱乐场所的问题。
            请用友好、专业的语气回答用户问题，回答要简洁明了、实用有帮助。
            
            用户问题：%s
            
            请直接给出回答，不要提及你是AI助手。
            """.formatted(question);
    }

    /**
     * 构建评论分析Prompt - 保持不变
     */
    private String buildCommentAnalysisPrompt(String comment) {
        return """
            请分析以下用户评论的情感倾向和关键信息，并以JSON格式返回结果：
            {
                "sentiment": "positive/negative/neutral",
                "pros": ["优点1", "优点2"],
                "cons": ["缺点1", "缺点2"],
                "summary": "一句话总结"
            }
            
            评论内容：%s
            
            要求：
            1. sentiment只能是positive、negative或neutral
            2. pros和cons最多3个项，没有时返回空数组
            3. 只返回JSON格式，不要其他内容
            """.formatted(comment);
    }

    /**
     * 解析评论分析结果 - 保持不变
     */
    private Map<String, Object> parseCommentAnalysisResult(String resultText) {
        try {
            // 清理结果中的多余内容
            String cleanResult = resultText.replace("```json", "").replace("```", "").trim();
            return JSON.parseObject(cleanResult, Map.class);
        } catch (Exception e) {
            System.out.println("❌ 解析评论分析结果失败，使用默认结果");
            return getDefaultAnalysisResult();
        }
    }

    /**
     * 默认评论分析结果 - 保持不变
     */
    private Map<String, Object> getDefaultAnalysisResult() {
        Map<String, Object> result = new HashMap<>();
        result.put("sentiment", "neutral");
        result.put("pros", Collections.emptyList());
        result.put("cons", Collections.emptyList());
        result.put("summary", "评论分析服务暂时不可用");
        return result;
    }

    /**
     * 备用回答 - 保持不变
     */
    private String getFallbackAnswer(String question) {
        // 简单的关键词匹配备用回答
        if (question.contains("咖啡") || question.contains("自习")) {
            return "附近有多家适合自习的咖啡店，如星巴克、漫咖啡等，环境舒适，WiFi稳定。";
        } else if (question.contains("美食") || question.contains("推荐")) {
            return "为您推荐周边的川菜馆、日料店和西餐厅，各有特色，满足不同口味需求。";
        } else {
            return "您好！我是校园生活助手，可以帮您查询商铺信息、推荐美食等。请告诉我您需要什么帮助？";
        }
    }


}