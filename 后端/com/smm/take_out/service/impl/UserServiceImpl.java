package com.smm.take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smm.take_out.Mapper.UserMapper;
import com.smm.take_out.entity.User;
import com.smm.take_out.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

        @Value("${spring.mail.username}")
        private String from;

        @Autowired
        JavaMailSender javaMailSender;

    /**
     * 发送验证码
     * @param to
     * @param subject
     * @param context
     */
        @Override
        public void sendMsg(String to, String subject, String context) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(from);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(context);

            javaMailSender.send(mailMessage);
        }
}
