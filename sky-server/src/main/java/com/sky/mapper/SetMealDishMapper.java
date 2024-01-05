package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品id查询对应套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    /**
     * 为某个套餐插入对应的菜品
     * @param setmealDishes
     * @return
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐setmealId删除对应的菜品
     * @param setmealIds
     * @return
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据套餐id查询对应的菜品
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getDishBySetmealId(Long setmealId);
}
