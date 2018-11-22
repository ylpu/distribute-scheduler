package com.yl.distribute.scheduler.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Optional;

public class SecurityContextUtils {

    /**
     * 获取当前登录的用户信息
     * 仅仅包含用户名和密码
     * @return
     */
    public static UserDetails getCurrentUser() {
        return Optional
                .of(
                        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                ).get();
    }
}
