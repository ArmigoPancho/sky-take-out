package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.UserService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;


    /**
     * 统计指定时间内的营业额数据
      * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO turnoverStatistic(LocalDate begin, LocalDate end){
        //new a dateList to add every date from begin date to end date
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while(!begin.equals(end)){  //if begin date equals to end date,stop the loop,otherwise,add the next date to dateList
            begin = begin.plusDays(1); //beginDate = beginDate + 1 day (next date)
            dateList.add(begin);
        }

        ArrayList<Double> turnoverList = new ArrayList<>(); //every date's total turnover
        //add every day's total turnover in a list: 2024-5-1 00:00:00< date < 2024-5-1 23:59:5999999  && statue = completed orders
        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null?0.0:turnover; //if there is no turnover on a given day,return 0.0,otherwise return turnover number，because there is no data in database,the total amount is null
            turnoverList.add(turnover);

        }
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();


    }

    /**
     * 统计指定时间内的用户统计:gather user statistics within a specified time frame
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO userStatistic(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //store the number of new users in one day
        List<Integer> newUserList = new ArrayList<>();
        //store the number of all users before current date（including current date）
        List<Integer> totalUserList = new ArrayList<>();

        Map map = new HashMap<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);


            map.put("endTime", endTime);
            //total users until today
            Integer totalUser = userMapper.countByMap(map);

            map.put("beginTime", beginTime);
            //new users in one day
            Integer newUser = userMapper.countByMap(map);

            newUserList.add(newUser);
            totalUserList.add(totalUser);

            map.remove("endTime");
            map.remove("beginTime");
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ','))
                .totalUserList(StringUtils.join(totalUserList,','))
                .newUserList(StringUtils.join(newUserList,','))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO orderStatistic(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //the number list of total orders every day
        List<Integer> orderCountList = new ArrayList<>();
        // the number list of valid orders every day
        List<Integer> validOrderCountList = new ArrayList<>();

        for(LocalDate date:dateList){
            LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);

            Integer orderCount = getOrderCount(beginTime,endTime,null);
            Integer validOrderCount = getOrderCount(beginTime,endTime,Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //total orders number
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //valid orders number
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0) {
            // calculate the rate of valid orders
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,','))
                .orderCountList(StringUtils.join(orderCountList,','))
                .validOrderCountList(StringUtils.join(validOrderCountList,','))
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 指定时间内销量Tpo10统计
     *
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime,endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList()); //get name from DTO object and transform it into a string list
        String nameList = StringUtils.join(names,',');

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers,',');

        //encapsulate the result data
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * inquiry the number of orders according to conditions
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end,Integer status){
        Map map = new HashMap<>();
        map.put("beginTime", begin);
        map.put("endTime", end);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }
}
