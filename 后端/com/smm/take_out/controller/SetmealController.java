package com.smm.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smm.take_out.commom.R;
import com.smm.take_out.dto.DishDto;
import com.smm.take_out.dto.SetMealDto;
import com.smm.take_out.entity.Category;
import com.smm.take_out.entity.Dish;
import com.smm.take_out.entity.Setmeal;
import com.smm.take_out.entity.SetmealDish;
import com.smm.take_out.service.CategoryService;
import com.smm.take_out.service.DishService;
import com.smm.take_out.service.SetmealDishService;
import com.smm.take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        //创建分页对象
//        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
//        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.like(name!=null,Setmeal::getName,name);
//        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
//        setmealService.page(pageInfo,queryWrapper);
//        return R.success(pageInfo);

        //创建分页对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);

        Page<SetMealDto> setMealDtoPage = new Page<>();

        //根据执行查询
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        //属性拷贝
        BeanUtils.copyProperties(pageInfo, setMealDtoPage);

        //遍历pageInfo里面的records
        List<Setmeal> records = pageInfo.getRecords();

        List<SetMealDto> list = records.stream().map((item) -> {
            //分类id
            SetMealDto setMealDto = new SetMealDto();
            BeanUtils.copyProperties(item, setMealDto);

            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setMealDto.setCategoryName(categoryName);
            }
            return setMealDto;
        }).collect(Collectors.toList());

        setMealDtoPage.setRecords(list);
        return R.success(setMealDtoPage);
    }

    /**
     * 新增套餐
     * @param setMealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetMealDto setMealDto){
        setmealService.saveWithDish(setMealDto);
        return R.success("套餐增加成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功！");
    }


    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
//    @PutMapping
//    public R<String> update(@RequestBody SetMealDto setmealDto){
//        log.info("修改套餐信息{}", setmealDto);
//        // 执行更新。
//        setmealService.updateWithSetmeal(setmealDto);
//        return R.success("套餐修改成功");
//    }

    @PutMapping
    public R<String> update(@RequestBody SetMealDto setmealDto){
        log.info("修改套餐信息{}", setmealDto);
        // 执行更新。
        setmealService.updateWithSetmeal(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> alterStatus(@PathVariable Integer status,Long[] ids){
        for (int i = 0; i < ids.length; i++) {
            Setmeal setmeal = setmealService.getById(ids[i]);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("修改套餐状态成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }

    /**
     * 根据id查询套餐信息
     *(套餐信息的回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetMealDto> getById(@PathVariable Long id) {
        log.info("根据id查询套餐信息:{}", id);
        // 调用service执行查询
        SetMealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }
}
