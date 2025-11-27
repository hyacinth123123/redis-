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

    @Value("${wenxin.api.secret-key:}")
    private String secretKey;

    private static final String CACHE_KEY_QA = "ai:qa:";
    private static final String CACHE_KEY_COMMENT = "ai:analysis:comment:";
    private static final String ACCESS_TOKEN_KEY = "wenxin:access_token";

    @Autowired
    private RestTemplate restTemplate;

    public WenxinAiService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取Access Token（带缓存）
     */
    private String getAccessToken() {
        // 检查缓存中的token
        String cachedToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
        if (cachedToken != null) {
            System.out.println("✅ 使用缓存的Access Token");
            return cachedToken;
        }

        try {
            String authUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" +
                    apiKey + "&client_secret=" + secretKey;

            System.out.println("🔑 尝试获取Access Token...");
            System.out.println("API Key: " + (apiKey.length() > 8 ? apiKey.substring(0, 8) + "..." : apiKey));
            System.out.println("Secret Key: " + (secretKey.length() > 8 ? secretKey.substring(0, 8) + "..." : secretKey));
            System.out.println("认证URL: " + authUrl);

            ResponseEntity<Map> response = restTemplate.getForEntity(authUrl, Map.class);
            Map<String, Object> responseBody = response.getBody();

            System.out.println("🔑 Access Token响应: " + responseBody);

            if (responseBody != null && responseBody.containsKey("access_token")) {
                String accessToken = (String) responseBody.get("access_token");
                // 缓存token（有效期通常为30天，我们缓存29天）
                redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, 29, TimeUnit.DAYS);
                System.out.println("✅ 成功获取Access Token");
                return accessToken;
            } else {
                System.out.println("❌ 获取Access Token失败，响应: " + responseBody);
            }
        } catch (Exception e) {
            System.out.println("❌ 获取百度Access Token失败: " + e.getMessage());
            e.printStackTrace();
        }

        throw new RuntimeException("无法获取百度文心一言访问令牌");
    }
    /**
     * 智能问答
     */
    public String answerQuestion(String question) {
        System.out.println("🤖 百度文心一言处理问题: " + question);


        // 检查是否配置了真实的API密钥
        if (apiKey == null || apiKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            System.out.println("⚠️ API密钥未配置，使用模拟回答");
            return Result.ok().getErrorMsg();
        }
        // 1. 缓存检查
        String cacheKey = CACHE_KEY_QA + question.hashCode();
        String cachedAnswer = redisTemplate.opsForValue().get(cacheKey);
        if (cachedAnswer != null) {
            System.out.println("✅ 从缓存返回答案");
            return cachedAnswer;
        }

        try {
            String accessToken = getAccessToken();
            String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + accessToken;

            // 2. 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", buildQAPrompt(question))
            ));
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 3. 调用API
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("result")) {
                String answer = (String) responseBody.get("result");
                System.out.println("✅ 百度文心一言返回答案: " + answer);

                // 4. 缓存结果
                redisTemplate.opsForValue().set(cacheKey, answer, 1, TimeUnit.HOURS);

                return answer;
            } else {
                System.out.println("❌ 百度API返回异常: " + responseBody);
                return getFallbackAnswer(question);
            }

        } catch (Exception e) {
            System.out.println("❌ 百度文心一言调用失败: " + e.getMessage());
            return getFallbackAnswer(question);
        }

    }

    /**
     * 评论情感分析
     */
    public Map<String, Object> analyzeComment(Long shopId, String comment) {
        System.out.println("📊 百度文心一言分析评论: " + comment);

        // 1. 缓存检查
        String cacheKey = CACHE_KEY_COMMENT + shopId + ":" + comment.hashCode();
        String cachedResult = redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return JSON.parseObject(cachedResult, Map.class);
        }

        try {
            String accessToken = getAccessToken();
            String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + accessToken;

            // 2. 构建请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", buildCommentAnalysisPrompt(comment))
            ));
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 3. 调用API
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("result")) {
                String resultText = (String) responseBody.get("result");
                System.out.println("✅ 百度文心一言返回分析结果: " + resultText);

                // 4. 解析JSON结果
                Map<String, Object> analysisResult = parseCommentAnalysisResult(resultText);

                // 5. 缓存结果
                redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(analysisResult), 24, TimeUnit.HOURS);

                return analysisResult;
            } else {
                System.out.println("❌ 百度API返回异常: " + responseBody);
                return getDefaultAnalysisResult();
            }

        } catch (Exception e) {
            System.out.println("❌ 评论分析失败: " + e.getMessage());
            return getDefaultAnalysisResult();
        }
    }

    /**
     * 构建问答Prompt
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
     * 构建评论分析Prompt
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
     * 解析评论分析结果
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
     * 默认评论分析结果
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
     * 备用回答
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