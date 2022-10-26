package com.smm.take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smm.take_out.commom.BaseContext;
import com.smm.take_out.commom.R;
import com.smm.take_out.entity.AddressBook;
import com.smm.take_out.entity.User;
import com.smm.take_out.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增用户
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> add(@RequestBody AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @param session
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook, HttpSession session) {

        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();

        Long userId = BaseContext.getCurrentId();

        queryWrapper.eq(AddressBook::getUserId,userId);

        queryWrapper.set(AddressBook::getIsDefault, 0);//先把所有地址改为0  然后再设置默认地址

        addressBookService.update(queryWrapper);

        addressBook.setIsDefault(1);

        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }



    /**
     * 根据地址id删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long ids){
        if (ids==null){
            return R.error("删除的地址id不能为空");
        }
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,ids);
        addressBookService.remove(queryWrapper);
        return R.success("删除地址成功");
    }


    /**
     * 修改收货地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        if (addressBook==null){
            return R.error("更新的地址不能为空");
        }
        addressBookService.updateById(addressBook);
        return R.success("地址更新成功！");
    }


    /**
     * 查询默认地址
     * @param session
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(HttpSession session) {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,userId);
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        if (one == null) {
            return R.error("该用户没有设置默认地址！！");
        } else {
            return R.success(one);
        }
    }

    /**
     * 根据id查找地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId, id);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有该用户地址");
        }
    }

    /**
     * 查询指定用户的所有地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getAll(AddressBook addressBook){
        Long userId = addressBook.getUserId();
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,userId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        if (list==null){
            return R.error("该用户没有地址！！");
        }else {
            return R.success(list);
        }

    }
}
