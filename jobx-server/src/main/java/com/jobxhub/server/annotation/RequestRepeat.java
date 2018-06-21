package com.jobxhub.server.annotation;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RequestRepeat {
    //是否需要返回提示页面
    boolean view() default false ;
}
