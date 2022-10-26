package com.smm.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smm.take_out.dto.SetMealDto;
import com.smm.take_out.entity.Setmeal;

import java.util.List;


public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setMealDto
     */
    public void saveWithDish(SetMealDto setMealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联 数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    public void updateWithSetmeal(SetMealDto setMealDto);

    SetMealDto getByIdWithDish(Long id);
}
