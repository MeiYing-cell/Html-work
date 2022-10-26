package com.smm.take_out.commom;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 * 用于MyBatisPlus的公共字段填充
 * ThreadLocal线程隔离
 * 使处在统一线程的，可以传递参数
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
