package com.collectorhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.collectorhub.dto.LoginFormDTO;
import com.collectorhub.dto.Result;
import com.collectorhub.entity.User;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IUserService extends IService<User> {

    Result senCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result sign();

    Result signCount();
}
