package com.smm.take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smm.take_out.Mapper.OrderDetailMapper;
import com.smm.take_out.entity.OrderDetail;
import com.smm.take_out.service.OrderDetailService;
import org.springframework.stereotype.Service;


@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
