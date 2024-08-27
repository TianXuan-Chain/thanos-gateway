package com.thanos.gateway.jsonrpc;

import java.lang.annotation.*;

/**
 * 类 RequestLimit.java的实现描述：
 *
 * @author dumaobing  on 2020/7/21 5:31 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface RequestLimit {
}
