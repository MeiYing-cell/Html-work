package com.smm.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smm.take_out.commom.BaseContext;
import com.smm.take_out.commom.R;
import com.smm.take_out.entity.ShoppingCart;
import com.smm.take_out.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加菜品或套餐  加入购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询当前菜品或套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        if (dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null) {
            //如果已经存在，原来基础加一
            cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //不存在，添加购物车  数量默认为一
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }


        return R.success(cartServiceOne);
    }


    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        return R.success(shoppingCartList);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, ShoppingCart::getUserId, userId);
        shoppingCartService.remove(queryWrapper);
        return R.success("购物车清除成功！！");
    }

    /**
     * 删除购物车菜品或套餐
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long userId = BaseContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        //如果减少的是菜品数量
        Long dishId = shoppingCart.getDishId();
        if (dishId!=null){
            //判断是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

            ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
            cartServiceOne.setNumber(cartServiceOne.getNumber()-1);

            if (cartServiceOne.getNumber()>0){
                //执行更新
                shoppingCartService.updateById(cartServiceOne);
            }else if (cartServiceOne.getNumber()==0){
                //执行删除
                shoppingCartService.removeById(cartServiceOne.getId());
            }else if (cartServiceOne.getNumber()<0){
                //操作异常
                return R.error("减少购物车菜品数量操作异常！！！");
            }
            return R.success(cartServiceOne);
        }
        //如果减少的是购物车套餐数量
        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId!=null){
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart cartServiceOneTwo = shoppingCartService.getOne(queryWrapper);
            cartServiceOneTwo.setNumber(cartServiceOneTwo.getNumber()-1);
            if (cartServiceOneTwo.getNumber()>0){
                //执行更新操作
                shoppingCartService.updateById(cartServiceOneTwo);
            }else if (cartServiceOneTwo.getNumber()==0){
                //执行删除操作
                shoppingCartService.removeById(cartServiceOneTwo.getId());
            }else if (cartServiceOneTwo.getNumber()<0){
                return R.error("减少购物车套餐数量操作异常！！！");
            }
            return R.success(cartServiceOneTwo);
        }
        return R.error("减少购物车套餐或菜品数量都异常！！！");
    }
}
