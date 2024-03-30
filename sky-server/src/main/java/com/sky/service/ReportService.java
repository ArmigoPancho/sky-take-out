package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Map;

public interface ReportService {

    /**
     * 统计指定时间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO turnoverStatistic(LocalDate begin,LocalDate end);


    /**
     * 统计指定时间内的用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO userStatistic(LocalDate begin, LocalDate end);


}
