package com.hmdp.controller;

import com.hmdp.dto.CommentAnalysisRequest;
import com.hmdp.dto.Result;
import com.hmdp.service.WenxinAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final WenxinAiService aiService;

    public AiController(WenxinAiService aiService) {
        this.aiService = aiService;
    }

    /**
     * 评论分析接口
     */
    @PostMapping("/analysis/comment")
    public Result analyzeComment(@RequestBody CommentAnalysisRequest request) {
        try {
            return Result.ok(aiService.analyzeComment(request.shopId(), request.comment()));
        } catch (Exception e) {
            return Result.fail("分析失败：" + e.getMessage());
        }
    }

    /**
     * 智能问答接口
     */
    @GetMapping("/qa")
    public Result answerQuestion(@RequestParam String question) {
        try {
            return Result.ok(aiService.answerQuestion(question));
        } catch (Exception e) {
            return Result.fail("问答失败：" + e.getMessage());
        }
    }
}