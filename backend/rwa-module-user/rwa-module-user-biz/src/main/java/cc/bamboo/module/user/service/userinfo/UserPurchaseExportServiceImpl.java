package cc.bamboo.module.user.service.userinfo;

import cc.bamboo.framework.common.pojo.PageParam;
import cc.bamboo.framework.excel.core.util.ExcelUtils;
import cc.bamboo.module.project.api.purchase.UserPurchaseSummaryApi;
import cc.bamboo.module.project.api.purchase.dto.UserPurchaseProjectAmountRespDTO;
import cc.bamboo.module.project.api.purchase.dto.UserPurchaseSummaryRespDTO;
import cc.bamboo.module.user.controller.admin.userinfo.vo.UserInfoPageReqVO;
import cc.bamboo.module.user.controller.admin.userinfo.vo.UserPurchaseExportRespVO;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;
import cc.bamboo.module.user.dal.mysql.userinfo.UserInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class UserPurchaseExportServiceImpl implements UserPurchaseExportService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private UserPurchaseSummaryApi userPurchaseSummaryApi;

    @Override
    public void exportUserPurchaseExcel(UserInfoPageReqVO pageReqVO, List<Long> userIds, HttpServletResponse response)
            throws IOException {
        List<UserInfoDO> users = getUsers(pageReqVO, userIds);
        if (users.isEmpty()) {
            ExcelUtils.write(response, "用户购买信息.xls", "数据", UserPurchaseExportRespVO.class, Collections.emptyList());
            return;
        }

        List<Long> ids = new ArrayList<>(users.size());
        for (UserInfoDO user : users) {
            if (user.getId() != null) {
                ids.add(user.getId());
            }
        }

        Map<Long, UserPurchaseSummaryRespDTO> summaryMap = new HashMap<>();
        if (!ids.isEmpty()) {
            List<UserPurchaseSummaryRespDTO> summaries = userPurchaseSummaryApi.getPurchaseSummaryList(ids).getCheckedData();
            for (UserPurchaseSummaryRespDTO summary : summaries) {
                if (summary != null && summary.getUserId() != null) {
                    summaryMap.put(summary.getUserId(), summary);
                }
            }
        }

        List<UserPurchaseExportRespVO> rows = new ArrayList<>(users.size());
        for (UserInfoDO user : users) {
            UserPurchaseExportRespVO row = new UserPurchaseExportRespVO();
            row.setUsername(getUsername(user));
            row.setMobile(maskMobile(user.getMobile()));
            row.setRegisterTime(user.getCreateTime());

            UserPurchaseSummaryRespDTO summary = summaryMap.get(user.getId());
            row.setPurchaseProjects(buildPurchaseProjects(summary));
            rows.add(row);
        }

        ExcelUtils.write(response, "用户购买信息.xls", "数据", UserPurchaseExportRespVO.class, rows);
    }

    private List<UserInfoDO> getUsers(UserInfoPageReqVO pageReqVO, List<Long> userIds) {
        if (!CollectionUtils.isEmpty(userIds)) {
            return userInfoMapper.selectBatchIds(userIds);
        }
        if (pageReqVO == null) {
            return Collections.emptyList();
        }
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        return userInfoMapper.selectPage(pageReqVO).getList();
    }

    private String getUsername(UserInfoDO user) {
        if (user == null) {
            return "";
        }
        if (user.getNickName() != null && !user.getNickName().trim().isEmpty()) {
            return user.getNickName();
        }
        if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            return user.getRealName();
        }
        return user.getId() == null ? "" : String.valueOf(user.getId());
    }

    private String maskMobile(String mobile) {
        if (mobile == null) {
            return "";
        }
        String value = mobile.trim();
        if (value.length() < 7) {
            return value;
        }
        if (value.length() >= 11) {
            return value.substring(0, 3) + "****" + value.substring(7);
        }
        return value.substring(0, 3) + "****" + value.substring(value.length() - 2);
    }

    private String buildPurchaseProjects(UserPurchaseSummaryRespDTO summary) {
        if (summary == null || CollectionUtils.isEmpty(summary.getProjectAmounts())) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (UserPurchaseProjectAmountRespDTO item : summary.getProjectAmounts()) {
            if (item == null) {
                continue;
            }
            String name = item.getProjectName();
            BigDecimal amount = item.getAmount() == null ? BigDecimal.ZERO : item.getAmount();
            String currency = item.getInvestmentCurrency();
            if (name == null || name.trim().isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(name).append("(").append(amount.toPlainString());
            if (currency != null && !currency.trim().isEmpty()) {
                sb.append(" ").append(currency.trim());
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
