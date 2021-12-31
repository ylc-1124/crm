package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.service.UserService;
import cn.sust.crm.utils.LoginUserUtil;
import cn.sust.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController extends BaseController {
    @Autowired
    private UserService userService;

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("welcome")
    public String welcome() {
        return "welcome";
    }

    @RequestMapping("main")
    public String main(HttpServletRequest request) {
        //从cookie中获取userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //通过userId查用户对象
        User user = userService.selectByPrimaryKey(userId);
        request.getSession().setAttribute("user", user);
        return "main";
    }
}
