package com.smm.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smm.take_out.entity.Orders;

import javax.servlet.http.HttpSession;


public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
