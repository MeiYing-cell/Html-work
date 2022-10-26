package com.smm.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smm.take_out.commom.R;
import com.smm.take_out.entity.User;
import com.smm.take_out.service.UserService;
import com.smm.take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;



@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

     @Autowired
     StringRedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    /**
     * 发送验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
        //获取邮箱账号
        String phone = user.getPhone();

        String subject = "瑞吉外卖";
        if (StringUtils.isNotEmpty(phone)) {
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            String context = "欢迎使用瑞吉外卖，登录验证码为：" + code + ",五分钟内有效，请妥善保管！";

            log.info("code=>{}", code);

            //发送邮件验证码
            userService.sendMsg(phone, subject, context);

//            //保存到session
//            session.setAttribute("phone", code);

            //将生成的验证码缓存到redis中
            redisTemplate.opsForValue().set("phone",code,5, TimeUnit.MINUTES);
            System.out.println(redisTemplate.opsForValue().get(phone));

            return R.success("验证码发送成功，请及时查看！");

        }
        return R.error("验证码发送失败，请重新输入！");
    }

    /**
     * 移动端用户登录
     *
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        //获取qq邮箱
        String mail = map.get("phone").toString();

        //获取验证码
        String mailCode = map.get("code").toString();

//        //从session中获取保存的验证码
//
//        Object codeSession = session.getAttribute("phone");
        Object codeSession = redisTemplate.opsForValue().get("phone");

        //进行验证码比对（页面提交的验证码和session中保存的验证码比对）
        if (codeSession != null && codeSession.equals(mailCode)) {
            //如果能比对成功，说明登陆成功
            //判断当前qq邮箱对应的用户是否为新用户，如果是就自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(mail != null, User::getPhone, mail);

            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //完成自动注册
                user = new User();
                user.setPhone(mail);
                user.setStatus(1);
                userService.save(user);
            }
            //保存到session
            session.setAttribute("user", user.getId());

            //如果用户登陆成功，删除Redis中缓存的验证码
            redisTemplate.delete(user.getPhone());

            return R.success(user);
        }
        return R.error("登录验证码发送失败！！");
    }

    /**
     * 退出功能
     * ①在controller中创建对应的处理方法来接受前端的请求，请求方式为post；
     * ②清理session中的用户id
     * ③返回结果（前端页面会进行跳转到登录页面）
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        //清理session中的用户id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
