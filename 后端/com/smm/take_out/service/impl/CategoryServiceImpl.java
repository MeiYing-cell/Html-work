package com.smm.take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smm.take_out.Mapper.CategoryMapper;
import com.smm.take_out.commom.CustomException;
import com.smm.take_out.entity.Category;
import com.smm.take_out.entity.Dish;
import com.smm.take_out.entity.Setmeal;
import com.smm.take_out.service.CategoryService;
import com.smm.take_out.service.DishService;
import com.smm.take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long ids) {

        //查询当前分类是否关联了菜品，如果已经关联，抛出业务异常
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(Dish::getCategoryId,ids);

        int count = dishService.count(queryWrapper);
        if (count>0){
            //已经关联了菜品，请先删除菜品
            //自定义异常
            throw new CustomException("当前分类下关联了菜品，请先删除分类下菜品，再删除分类！");
        }



        //查询当前分类是否关联了套餐，如果已经关联，抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        //查询
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1>0){
            //已经关联了套餐，请先删除套餐
            //自定义异常
            throw new CustomException("当前分类下关联了菜品，请先删除分类下菜品，再删除分类！");

        }
       //正常删除分类
        super.removeById(ids);
    }
}
