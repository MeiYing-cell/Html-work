package com.smm.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smm.take_out.entity.Category;
import org.springframework.stereotype.Service;


public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
