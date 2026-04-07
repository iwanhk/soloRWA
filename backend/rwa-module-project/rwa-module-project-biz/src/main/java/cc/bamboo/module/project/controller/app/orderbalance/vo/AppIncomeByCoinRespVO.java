package cc.bamboo.module.project.controller.app.orderbalance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Hus
 * @version 1.0
 * @date 2026/2/5 16:25
 * @description
 */
@Data
public class AppIncomeByCoinRespVO {
    private LocalDate incomeDate;
    private String coinCode;
    private BigDecimal total;
}
