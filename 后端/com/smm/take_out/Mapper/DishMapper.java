package com.smm.take_out.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smm.take_out.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
