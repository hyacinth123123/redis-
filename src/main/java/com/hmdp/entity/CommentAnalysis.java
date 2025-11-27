package com.hmdp.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tb_comment_analysis")
public class CommentAnalysis implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long commentId; // 关联BlogComments的id
    private String sentiment; // positive/negative/neutral
    private String pros; // 用逗号分隔的优点，如"环境好,服务佳"
    private String cons; // 用逗号分隔的缺点
    private String summary;
    private LocalDateTime createTime;
}
