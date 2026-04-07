package cc.bamboo.module.project.controller.admin.orderdailyincome.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/1/23 11:45
 * @description
 */
@Data
public class OrderDailyIncomeDistributeReqVO {
    /**
     * 收益日期
     */
    private LocalDate incomeDate;
}
