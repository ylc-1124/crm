package cn.sust.crm.aspect;

import cn.sust.crm.annotation.RequiredPermission;
import cn.sust.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {
    @Autowired
    private HttpSession session;

    @Around(value = "@annotation(cn.sust.crm.annotation.RequiredPermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        //执行方法前从session中获取当前用户所有的权限
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        //判断用户是否拥有访问该方法的权限
        if (permissions == null || permissions.size() < 1) {
            //抛出认证异常
            throw new AuthException();
        }
        //拿到切入点的签名
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //得到方法上的注解
        RequiredPermission annotation =
                methodSignature.getMethod().getDeclaredAnnotation(RequiredPermission.class);

        //用户若无该权限码，抛出异常
        if (!permissions.contains(annotation.code())) {
            throw new AuthException();
        }
        result = pjp.proceed();
        return result;
    }
}
