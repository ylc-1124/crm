package cn.sust.crm.interceptor;

import cn.sust.crm.dao.UserMapper;
import cn.sust.crm.exceptions.NoLoginException;
import cn.sust.crm.service.UserService;
import cn.sust.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非法访问拦截
 */
public class NoLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    /**
     * 拦截请求判断是否是登录状态
     * 1、判断cookie是否存在用户id
     * 2、数据库中是否存在指定用户id值
     * 如果用户是登录状态，放行，否则抛出未登录异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //如果用户未登录
        if (userId == null || userMapper.selectByPrimaryKey(userId) == null) {
            throw new NoLoginException();
        }
        return true;
    }
}
