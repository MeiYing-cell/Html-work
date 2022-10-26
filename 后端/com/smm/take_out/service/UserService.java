package com.smm.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smm.take_out.entity.User;


public interface UserService extends IService<User> {
    //发送验证码
    void sendMsg(String phone, String subject, String context);
}
