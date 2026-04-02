package com.collectorhub.service;

import com.collectorhub.dto.Result;
import com.collectorhub.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IBlogService extends IService<Blog> {

    Result saveBlog(Blog blog);

    Result queryBlogById(Long id);

    Result queryHotBlog(Integer current);

    Result likeBlog(Long id);

    Result queryBlogLikes(Long id);

    Result queryBlogOfFollow(Long max, Integer offset);
}
