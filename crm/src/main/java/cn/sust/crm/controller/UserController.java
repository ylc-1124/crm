package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.base.ResultInfo;
import cn.sust.crm.exceptions.ParamsException;
import cn.sust.crm.model.UserModel;
import cn.sust.crm.service.UserService;
import cn.sust.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 用户修改密码
     */
    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,
                                         String oldPassword,
                                         String newPassword,
                                         String repeatPassword) {
        ResultInfo resultInfo = new ResultInfo();
        //获取userId
        int userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //调用修改密码的方法
        userService.updatePassword(userId, oldPassword, newPassword, repeatPassword);

        return resultInfo;
    }

    /**
     * 登录功能
     */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd) {
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(userName, userPwd);
        //设置resultInfo的result，将数据返回给请求
        resultInfo.setResult(userModel);

        return resultInfo;
    }

    /**
     * 进入修改密码的页面
     */
    @GetMapping("toPasswordPage")
    public String toPasswordPage() {
        return "user/password";
    }
}
