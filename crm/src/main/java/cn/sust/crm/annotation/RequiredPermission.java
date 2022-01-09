package cn.sust.crm.annotation;

import java.lang.annotation.*;

/**
 * 设置方法所需要的权限码
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredPermission {
    //权限码
    String code() default "";
}
