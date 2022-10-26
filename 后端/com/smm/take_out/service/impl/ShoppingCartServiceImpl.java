package com.smm.take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smm.take_out.Mapper.ShoppingCartMapper;
import com.smm.take_out.entity.ShoppingCart;
import com.smm.take_out.service.ShoppingCartService;
import org.springframework.stereotype.Service;


@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
