package cc.bamboo.module.project.api.purchase;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.project.api.purchase.dto.UserPurchaseProjectAmountRespDTO;
import cc.bamboo.module.project.api.purchase.dto.UserPurchaseSummaryRespDTO;
import cc.bamboo.module.project.dal.mysql.projectorder.ProjectOrderMapper;
import cc.bamboo.module.project.enums.OrderStatusEnum;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class UserPurchaseSummaryApiImpl implements UserPurchaseSummaryApi {

    @Resource
    private ProjectOrderMapper projectOrderMapper;

    @Override
    public CommonResult<List<UserPurchaseSummaryRespDTO>> getPurchaseSummaryList(Collection<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return success(Collections.emptyList());
        }

        List<UserPurchaseProjectAmountRespDTO> rows = projectOrderMapper.selectUserPurchaseAmountByUserIds(userIds,
                OrderStatusEnum.APPROVED.getStatus(), OrderStatusEnum.ENDED.getStatus());

        Map<Long, List<UserPurchaseProjectAmountRespDTO>> map = new HashMap<>();
        for (UserPurchaseProjectAmountRespDTO row : rows) {
            if (row == null || row.getUserId() == null) {
                continue;
            }
            map.computeIfAbsent(row.getUserId(), k -> new ArrayList<>()).add(row);
        }

        List<UserPurchaseSummaryRespDTO> result = new ArrayList<>(userIds.size());
        for (Long userId : userIds) {
            UserPurchaseSummaryRespDTO summary = new UserPurchaseSummaryRespDTO();
            summary.setUserId(userId);
            List<UserPurchaseProjectAmountRespDTO> items = map.getOrDefault(userId, Collections.emptyList());
            summary.setProjectAmounts(items);
            BigDecimal total = BigDecimal.ZERO;
            for (UserPurchaseProjectAmountRespDTO item : items) {
                if (item != null && item.getAmount() != null) {
                    total = total.add(item.getAmount());
                }
            }
            summary.setTotalAmount(total);
            result.add(summary);
        }
        return success(result);
    }
}

