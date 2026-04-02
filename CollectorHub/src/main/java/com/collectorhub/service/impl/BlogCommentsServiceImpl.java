package com.collectorhub.service.impl;

import com.collectorhub.entity.BlogComments;
import com.collectorhub.mapper.BlogCommentsMapper;
import com.collectorhub.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

    @Override
    public void analyzeCommentAsync(Long commentId, String content, Long shopId) {

    }
}
