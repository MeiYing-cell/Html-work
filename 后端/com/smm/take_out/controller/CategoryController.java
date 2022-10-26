package com.smm.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smm.take_out.commom.R;
import com.smm.take_out.entity.Category;
import com.smm.take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    public CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category, HttpServletRequest request){
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        category.setCreateUser(empId);
//        category.setUpdateUser(empId);
        categoryService.save(category);
        return R.success("保存成功！！");
    }

    /**
     * 菜品分类分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //创建分页查询对象
        Page<Category> pageInfo=new Page<>(page,pageSize);
        //条件构造器对象
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Category::getSort);
        //进行分页查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除菜品分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleted(Long ids) {
        categoryService.remove(ids);
        return R.success("分类信息删除成功！！");
    }

    /**
     * 更新
     * @param category
     * @param request
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category,HttpServletRequest request){

//        category.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        category.setUpdateUser(empId);

        categoryService.updateById(category);
        return R.success("修改菜品分类成功！");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")//返回类型是下拉框里的数组
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);

        return R.success(list);

    }
}
