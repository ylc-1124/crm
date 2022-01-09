package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.service.PermissionService;
import cn.sust.crm.service.UserService;
import cn.sust.crm.utils.LoginUserUtil;
import cn.sust.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

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

        //通过当前登录用户的ID查询用户拥有的资源列表（查询对应资源的授权码）
        List<String> permissions = permissionService.queryPermissionByUserId(userId);
        //将集合放入session域中
        request.getSession().setAttribute("permissions", permissions);
        return "main";
    }
}
