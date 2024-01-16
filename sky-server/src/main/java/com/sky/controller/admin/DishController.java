package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理某个新增菜品的缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCash(key);

        return Result.success();

    }

    /**
     *  菜单分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询");
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}",ids);
        dishService.deleteBatch(ids);
        //以dish_开头的菜品缓存都删除
        cleanCash("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询对应菜品数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品数据")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVO = dishService.getByIdwithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 更新菜品数据
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("菜品修改")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        //简单处理：以dish_开头的菜品缓存都删除
        cleanCash("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result< List<Dish> > getByCategoryId(Long categoryId){
        List<Dish> dishlist = dishService.getByCategoryId(categoryId);
        //以dish_开头的菜品缓存都删除
        cleanCash("dish_*");
        return Result.success(dishlist);
    }

    /**
     * 菜品的起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status,id);
        cleanCash("clash_*");
        return Result.success();
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCash(String pattern){
        //以pattern为模式的菜品缓存都删除
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
