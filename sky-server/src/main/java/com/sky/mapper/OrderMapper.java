package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.DishVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单表数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**
     * 用于替换微信支付更新数据库状态的问题
     * @param OrderStatus
     */
    @Update("update orders set status = #{OrderStatus},pay_status = #{PayStatus} ,checkout_time = #{check_out_time} where number = #{number}")
    void updateStatus(Integer OrderStatus, Integer PayStatus, LocalDateTime check_out_time, Long number);

    /**
     * 历史订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id获取订单信息
     * @param id
     * @return
     */
    @Select("select  * from orders where id = #{id}")
    Orders getOrderById(Long id);

    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 获取超时的订单
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from  orders where  status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndTimeoutLT(Integer status,LocalDateTime orderTime);

    /**
     * inquiry every day's total turnover查询每天的营业额
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * inquiry every day's orders' number according to conditions 根据动态条件查询订单数据
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
