package com.sky.service.impl;


import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
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

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;


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
            turnover = turnover == null?0.0:turnover; //if there is no turnover on a given day,return 0.0,otherwise return turnover number
            turnoverList.add(turnover);

        }
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();


    }
}
