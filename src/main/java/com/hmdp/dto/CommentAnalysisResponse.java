package com.hmdp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentAnalysisResponse {
    private String sentiment;       // positive/negative/neutral
    private List<String> pros;      // 优点列表
    private List<String> cons;      // 缺点列表
    private String summary;         // 总结
}