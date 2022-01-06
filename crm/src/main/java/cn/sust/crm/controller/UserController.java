package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.base.ResultInfo;
import cn.sust.crm.exceptions.ParamsException;
import cn.sust.crm.model.UserModel;
import cn.sust.crm.query.UserQuery;
import cn.sust.crm.service.UserService;
import cn.sust.crm.utils.LoginUserUtil;
import cn.sust.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 查询所有销售人员
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }

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

    /**
     * 分页多条件查询
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> selectByParams(UserQuery userQuery) {
        return userService.queryByParamsForTable(userQuery);
    }

    /**
     * 用户管理主页
     */
    @RequestMapping("index")
    public String index() {
        return "user/user";
    }

    /**
     * 添加用户
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user) {
        userService.addUser(user);
        return success("用户添加成功!");
    }

    /**
     * 更新用户
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("用户更新成功!");
    }

    /**
     * 打开用户添加/修改的页面
     */
    @RequestMapping("toAddOrUpdateUserPage")
    public String toAddOrUpdateUserPage(Integer id, Model model) {
        //判断id是否为空，不为空表示更新操作，查询用户对象
        if (id != null) {
            User user = userService.selectByPrimaryKey(id);
            //数据设置到请求域中
            model.addAttribute("userInfo", user);
        }
        return "user/add_update";
    }

    /**
     * 用户删除
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids) {
        userService.deleteByIds(ids);
        return success("用户删除成功!");
    }
}
