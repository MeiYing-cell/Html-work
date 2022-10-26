package com.smm.take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smm.take_out.Mapper.DishMapper;
import com.smm.take_out.dto.DishDto;
import com.smm.take_out.entity.Dish;
import com.smm.take_out.entity.DishFlavor;
import com.smm.take_out.entity.SetmealDish;
import com.smm.take_out.service.DishFlavorService;
import com.smm.take_out.service.DishService;
import com.smm.take_out.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);


        //保存flavors到菜品口味表dishflavor
        //获取到菜品id  DishDto继承了Dish
        Long dishDtoId = dishDto.getId();
        //菜品口味  获取到所有的口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历  为菜品id赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor  操作DishFlavor类  需注入DishFlavorService
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查
        Dish dish = this.getById(id);

        //属性拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //单独为dishDto里面的flavors赋值
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 根据前端返回的dishDto，更新
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据--dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据--dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历flavors 把里面的每一项拿出来赋值
        flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据前端传的dishId  删除dish表数据
     * 删除dish_flavor表中数据
     * 删除setmeal_dish表中数据
     * @param ids
     */
    @Override
    public void remove(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        //删除菜品表
        dishService.remove(queryWrapper);

        //删除dish_flavor表
        LambdaQueryWrapper<DishFlavor> dishFlavor=new LambdaQueryWrapper<>();
        dishFlavor.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishFlavor);
        //删除setmeal_dish表
        LambdaQueryWrapper<SetmealDish> setmealDish=new LambdaQueryWrapper<>();
        setmealDish.in(SetmealDish::getDishId,ids);
        setmealDishService.remove(setmealDish);
    }
}
