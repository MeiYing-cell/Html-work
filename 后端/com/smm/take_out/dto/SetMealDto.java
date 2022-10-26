package com.smm.take_out.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.smm.take_out.entity.Setmeal;
import com.smm.take_out.entity.SetmealDish;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class SetMealDto extends Setmeal {
   private List<SetmealDish> setmealDishes;
    private String categoryName;
}
