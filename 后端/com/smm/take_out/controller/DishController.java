package com.smm.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smm.take_out.commom.R;
import com.smm.take_out.dto.DishDto;
import com.smm.take_out.entity.Category;
import com.smm.take_out.entity.Dish;
import com.smm.take_out.entity.DishFlavor;
import com.smm.take_out.service.CategoryService;
import com.smm.take_out.service.DishFlavorService;
import com.smm.take_out.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")//get不加注解也能取得参数
    public R<Page> page(int page, int pageSize, String name) {
//        //构造分页构造器
//        Page pageInfo=new Page(page,pageSize);
//        //构造条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
//        queryWrapper.orderByDesc(Dish::getUpdateTime);
//        dishService.page(pageInfo,queryWrapper);
//        return R.success(pageInfo);


        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     *
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功！！！");
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("更新菜品成功！！！");
    }

//    @PostMapping("/status/{status}")
//    public R<String> alterStatues(@RequestBody Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getId,dish.getId());
//        dish = dishService.getOne(queryWrapper);
//        dish.setStatus(dish.getStatus());
//        return R.success("修改状态成功！！");
//    }

    /**
     * 修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PutMapping("/status/{status}")
    public R<String> onOrClose(@PathVariable Integer status, Long[] ids){
        for (int i = 0; i < ids.length; i++) {
            // 获取菜品
            Dish dish = dishService.getById(ids[i]);
            dish.setStatus(status);
            // 修改状态
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }



//    /**
//     * 根据条件categoryid查询对应的菜品数据
//     * @param dish
//     * @return
//     */
//    @GetMapping("/list")
//    //当前端传来的值不是完整的对象，只包含了Req的部分参数，不需要@RequestBody
//    //当前端传来的是一个完整的对象，需要@RequestBody
//    public R<List<Dish>> list(Dish dish,String name){
//
//        //条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//
//        queryWrapper.like(name!=null,Dish::getName,dish.getName());
//        queryWrapper.eq(Dish::getStatus,1L);
//
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }



    @GetMapping("/list")
    //当前端传来的值不是完整的对象，只包含了Req的部分参数，不需要@RequestBody
    //当前端传来的是一个完整的对象，需要@RequestBody
    public R<List<DishDto>> list(Dish dish,String name){
        List<DishDto> dishDtoList=null;
//        //动态构造key
//        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();//dish_13959617657_1
//
//
//        /**
//         * 这个接收的string，不能转换为List<DishDto>
//         */
//        //先从Redis中获取缓存数据
//        dishDtoList=stringRedisTemplate.opsForValue().get(key);
//        if (dishDtoList!=null){
//            //如果存在，直接返回，无需查询数据库
//            return R.success(dishDtoList);
//        }




        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();

        //条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.like(name!=null,Dish::getName,dish.getName());
        queryWrapper.eq(Dish::getStatus,1L);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        //处理查询出来的dish  list
        dishDtoList=list.stream().map((item)->{
            //copy属性
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            //根据菜品id查找口味list
            LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            flavorLambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());

            List<DishFlavor> dishFlavors = dishFlavorService.list(flavorLambdaQueryWrapper);
            //给口味追加赋值
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

//        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
//        stringRedisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    /**
     * 根据id删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleted(@RequestParam List<Long> ids){
        dishService.remove(ids);
        return R.success("删除成功！！");
    }

}
