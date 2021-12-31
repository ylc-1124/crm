package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.UserMapper;
import cn.sust.crm.model.UserModel;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.utils.Md5Util;
import cn.sust.crm.utils.UserIDBase64;
import cn.sust.crm.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends BaseService<User, Integer> {
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void updatePassword(Integer userId, String oldPwd,
                               String newPwd, String repeatPwd) {
        User user = userMapper.selectByPrimaryKey(userId);
        //判断用户是否存在
        AssertUtil.isTrue(user == null, "待更新记录不存在");
        //参数校验
        checkPasswordParams(user, oldPwd, newPwd, repeatPwd);
        //设置新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        //执行更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1,
                "修改密码失败");
    }

    /**
     * 修改密码的参数校验
     */
    private void checkPasswordParams(User user, String oldPwd,
                                     String newPwd, String repeatPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "原始密码不能为空");
        //判断输入的原始密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)),//加密后比较
                "原始密码不正确");
        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空");
        //判断新密码和旧密码是否相同，不允许相同
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码和旧密码不能相同");
        //判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd), "确认密码不能为空");
        //判断确认密码是否与新密码相同
        AssertUtil.isTrue(!newPwd.equals(repeatPwd),"确认密码与新密码不一致");
    }

    /**
     * 用户登录操作
     */
    public UserModel userLogin(String userName, String userPwd) {
        //1、参数校验
        checkLoginParams(userName, userPwd);
        //2、数据库查询
        User user = userMapper.queryUserByName(userName);
        AssertUtil.isTrue(user == null, "用户不存在!");
        //3、比较密码：
        checkUserPwd(userPwd, user.getUserPwd());
        //4、登录成功,返回构建用户对象
        return buildUserModel(user);

    }

    private UserModel buildUserModel(User user) {
        UserModel userModel = new UserModel();
        //设置加密的userId
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 密码判断：
     * 将浏览器传入的密码先加密，再和数据库中密码进行比较
     */
    private void checkUserPwd(String userPwd, String pwd) {
        //浏览器传入的密码进行加密
        userPwd = Md5Util.encode(userPwd);
        //判断密码是否相等
        AssertUtil.isTrue(!pwd.equals(userPwd),"密码错误!");
    }

    /**
     * 参数校验
     */
    private void checkLoginParams(String userName, String userPwd) {
        //验证用户姓名
        AssertUtil.isTrue(userName == null, "用户姓名不能为空!");
        //验证密码
        AssertUtil.isTrue(userPwd == null, "用户密码不能为空!");
    }
}
