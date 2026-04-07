package cc.bamboo.module.project.dal.mysql.userprojectbalance.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectIncomeAggRespDTO {

    private Long projectId;

    private BigDecimal totalIncome;

    private BigDecimal withdrawnDividend;
}

