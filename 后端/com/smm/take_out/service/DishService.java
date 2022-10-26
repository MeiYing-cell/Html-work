package com.smm.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smm.take_out.dto.DishDto;
import com.smm.take_out.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish>{
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表，dish,dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时更新dish_flavor表
    void updateWithFlavor(DishDto dishDto);

    //删除菜品表，同时删除口味表
    public void remove(List<Long> ids);
}
