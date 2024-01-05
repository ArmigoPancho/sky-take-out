package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    void insert(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult getSetMeal(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据套菜id查询套餐数据
     * @param id
     * @return
     */
    SetmealVO getSetmealById(Long id);

    /**
     * 套餐修改
     * @param setmealDTO
     * @return
     */
    void updateWithSetmealDish(SetmealDTO setmealDTO);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    void startOrStop(Integer status, Long id);
}
