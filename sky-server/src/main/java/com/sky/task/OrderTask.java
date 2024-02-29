package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时类任务，定时处理订单状态process orders periodically
 */
@Slf4j
@Component
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单process the TimeOut Orders
     */
    @Scheduled(cron = "0 * * * * ?") //每分钟触发一次do this task every one minute
    public void processTimeOutOrder(){
        log.info("但是处理超时未付款的订单：{}",LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);  //当前时间减去15分钟The current time minus 15 minutes
        List<Orders> ordersList = orderMapper.getByStatusAndTimeoutLT(Orders.PENDING_PAYMENT,time); //get the timeout orders(>15mins)

        if(ordersList != null && ordersList.size() >0){
            for(Orders order:ordersList){
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("订单超时，自动取消。");
                orderMapper.update(order);
            }
        }

    }

    /**
     * 处理一直处于派送中的订单to handle orders that are consistently in "delivery in process" status(4)
     */
    @Scheduled(cron = "0 0 1 * * ?")//每天凌晨1点处理0点之前的派送中超时订单Process orders being delivered that before 00:00 at 1:00 AM every day.
    public void processDeliveryOrder(){
        log.info("处理派送中但实际已完成的订单：{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);  //当前1:00 AM减去60分钟The current time 1:00 minus 60 minutes=00:00
        List<Orders> ordersList = orderMapper.getByStatusAndTimeoutLT(Orders.DELIVERY_IN_PROGRESS,time); //get the timeout orders
        if(ordersList != null && ordersList.size() >0){
            for(Orders order:ordersList){
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
