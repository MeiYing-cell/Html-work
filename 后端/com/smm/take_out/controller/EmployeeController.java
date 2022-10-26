package com.smm.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smm.take_out.commom.R;
import com.smm.take_out.entity.Employee;
import com.smm.take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1.将页面提交的密码password进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3.如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登陆失败");
        }
        //4.密码比对，如果不一致返回登录失败结果
        if (!(emp.getPassword().equals(password))) {
            return R.error("登录失败！");
        }
        //5.查看员工状态，如果为已禁用状态，则返回员工已经用结果
        if (emp.getStatus() == 0) {
            return R.error("员工状态已禁用");
        }
        //6.登陆成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("新增员工，员工信息：{}", employee.toString());
        //设置初始密码，需进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功！");
    }

    /**
     * 员工信息页面分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }

    /**
     * 根据员工修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        long id = Thread.currentThread().getId();
        log.info("==========controller当前线程为：{}", id);



        return R.success("员工信息修改成功！");
    }


    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee e = employeeService.getById(id);
        if (e != null) {
            return R.success(e);
        }
        return R.error("没有查询到对应员工信息");

    }
}


