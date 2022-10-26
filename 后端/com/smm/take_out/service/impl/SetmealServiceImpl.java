package com.smm.take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smm.take_out.Mapper.SetmealMapper;
import com.smm.take_out.commom.CustomException;
import com.smm.take_out.dto.SetMealDto;
import com.smm.take_out.entity.Setmeal;
import com.smm.take_out.entity.SetmealDish;
import com.smm.take_out.service.SetmealDishService;
import com.smm.take_out.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {



   @Autowired
   private SetmealService setmealService;

   @Autowired
    private SetmealDishService setmealDishService;

   /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setMealDto
     */
   @Transactional
    @Override
    public void saveWithDish(SetMealDto setMealDto) {
        //保存套餐的基本信息 setmeal表 执行insert操作
        setmealService.save(setMealDto);

        //保存套餐和菜品的关联信息 setmeal_dish 执行insert操作
        List<SetmealDish> setmealDishes = setMealDto.getSetmealDishes();
        //遍历setmealDishes 并为里面的dishid赋值
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setMealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        //不能删除，抛出业务异常
        int count=setmealService.count(queryWrapper);
        if (count>0){
            throw new CustomException("套餐正在售卖中，，不能删除！！");
        }

        //如果可以删除，先删除套餐表中的数据 setmeal表
        setmealService.removeByIds(ids);

        //删除关系表中的数据 setmeal_dish表
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);

    }


    @Override
    public void updateWithSetmeal(SetMealDto setmealDto) {
        // 保存setmeal表中的基本数据。
        this.updateById(setmealDto);
        // 先删除原来的套餐所对应的菜品数据。
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        // 更新套餐关联菜品信息。setmeal_dish表。
        // Field 'setmeal_id' doesn't have a default value] with root cause
        // 所以需要处理setmeal_id字段。
        // 先获得套餐所对应的菜品集合。
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //每一个item为SetmealDish对象。
        setmealDishes = setmealDishes.stream().map((item) -> {
            //设置setmeal_id字段。
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 重新保存套餐对应菜品数据
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public SetMealDto getByIdWithDish(Long id) {
        // 根据id查询setmeal表中的基本信息
        Setmeal setmeal = this.getById(id);
        SetMealDto setmealDto = new SetMealDto();
        // 对象拷贝。
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查询关联表setmeal_dish的菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        //设置套餐菜品属性
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }
}
