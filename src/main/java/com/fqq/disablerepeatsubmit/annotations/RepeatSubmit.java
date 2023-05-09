package com.fqq.disablerepeatsubmit.annotations;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    /**
     * <p> 2022/12/18 21:18 </p>
     *
     * @return 间隔时间 (s)
     */
    int interval() default 5000;

    /**
     * <p> 2022/12/18 21:18 </p>
     *
     * @return 提示信息
     */
    String massage() default "你重复提交了一样的表单，请稍后重试！";
}
