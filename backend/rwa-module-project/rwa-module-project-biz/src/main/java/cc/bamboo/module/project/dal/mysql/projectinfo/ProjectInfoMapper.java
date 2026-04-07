package cc.bamboo.module.project.dal.mysql.projectinfo;

import cc.bamboo.framework.common.pojo.PageResult;
import cc.bamboo.framework.mybatis.core.mapper.BaseMapperX;
import cc.bamboo.framework.mybatis.core.query.LambdaQueryWrapperX;
import cc.bamboo.module.project.controller.admin.projectinfo.vo.ProjectInfoPageReqVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppProjectPaymentInfoRespVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AppUserBankRespVO;
import cc.bamboo.module.project.controller.app.projectinfo.vo.AssetTypeCountRespVO;
import cc.bamboo.module.project.dal.dataobject.projectbill.ProjectBillDO;
import cc.bamboo.module.project.dal.dataobject.projectinfo.ProjectInfoDO;
import cc.bamboo.module.project.enums.AuditStatusEnum;
import cc.bamboo.module.project.enums.RunStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 项目核心表（基础+状态） Mapper
 *
 * @author Swolf
 */
@Mapper
public interface ProjectInfoMapper extends BaseMapperX<ProjectInfoDO> {

        /**
         * 使用乐观锁更新库存（基于库存数量）
         * 
         * @param projectId 项目ID
         * @param quantity  扣减数量
         * @return 更新行数（0表示库存不足，1表示更新成功）
         */
        @Update("UPDATE biz_project_info SET remaining_quantity = remaining_quantity - #{quantity} , sales_quantity = sales_quantity + #{quantity} " +
                        "WHERE project_id= #{projectId} AND remaining_quantity >= #{quantity}")
        int updateStockWithOptimisticLock(@Param("projectId") Long projectId,
                        @Param("quantity") Integer quantity);

        /**
         * 恢复库存（增加剩余数量）
         * 用于订单取消时恢复库存
         * 
         * @param projectId 项目ID
         * @param quantity  恢复数量
         * @return 更新行数
         */
        @Update("UPDATE biz_project_info SET remaining_quantity = remaining_quantity + #{quantity} , sales_quantity = sales_quantity - #{quantity} " +
                        "WHERE project_id= #{projectId} AND sales_quantity >= #{quantity}")
        int restoreStock(@Param("projectId") Long projectId,
                        @Param("quantity") Integer quantity);

        @Update("UPDATE biz_project_info SET chain_status = #{chainStatus}, chain_tokens_project_id= #{chainTokensId}, chain_token_address = #{chainTokenAddress} "
                        +
                        "WHERE project_id= #{projectId}")
        int updateChainInfo(@Param("projectId") Long projectId,
                        @Param("chainStatus") Integer chainStatus,
                        @Param("chainTokensId") Long chainTokensId,
                        @Param("chainTokenAddress") String chainTokenAddress);

        default PageResult<ProjectInfoDO> selectPage(ProjectInfoPageReqVO reqVO) {
                return selectPage(reqVO, new LambdaQueryWrapperX<ProjectInfoDO>()
                                .likeIfPresent(ProjectInfoDO::getProjectName, reqVO.getProjectName())
                                .eqIfPresent(ProjectInfoDO::getProjectType, reqVO.getProjectType())
                                .eqIfPresent(ProjectInfoDO::getAssetType, reqVO.getAssetType())
                                .eqIfPresent(ProjectInfoDO::getPublisherUserId, reqVO.getPublisherUserId())
                                .likeIfPresent(ProjectInfoDO::getPublisherCompanyName, reqVO.getPublisherCompanyName())
                                .betweenIfPresent(ProjectInfoDO::getCreateTime, reqVO.getCreateTime())
                                .eqIfPresent(ProjectInfoDO::getIssueQuantity, reqVO.getIssueQuantity())
                                .eqIfPresent(ProjectInfoDO::getIssueUnitPrice, reqVO.getIssueUnitPrice())
                                .eqIfPresent(ProjectInfoDO::getRemainingQuantity, reqVO.getRemainingQuantity())
                                .eqIfPresent(ProjectInfoDO::getExpectedAnnualReturn, reqVO.getExpectedAnnualReturn())
                                .eqIfPresent(ProjectInfoDO::getMinimumPurchase, reqVO.getMinimumPurchase())
                                .eqIfPresent(ProjectInfoDO::getIssueChainId, reqVO.getIssueChainId())
                                .betweenIfPresent(ProjectInfoDO::getLockStartTime, reqVO.getLockStartTime())
                                .betweenIfPresent(ProjectInfoDO::getLockEndTime, reqVO.getLockEndTime())
                                .eqIfPresent(ProjectInfoDO::getProjectStatus, reqVO.getProjectStatus())
                                .eqIfPresent(ProjectInfoDO::getProjectIntro, reqVO.getProjectIntro())
                                .eqIfPresent(ProjectInfoDO::getProjectFileUrls, reqVO.getProjectFileUrls())
                                .eqIfPresent(ProjectInfoDO::getAuditStatus, reqVO.getAuditStatus())
                                .eqIfPresent(ProjectInfoDO::getEarlyRedemptionFeeJson,
                                                reqVO.getEarlyRedemptionFeeJson())
                                .eqIfPresent(ProjectInfoDO::getProjectImageUrls, reqVO.getProjectImageUrls())
                                .eqIfPresent(ProjectInfoDO::getProjectVideoUrl, reqVO.getProjectVideoUrl())
                                .orderByDesc(ProjectInfoDO::getProjectId));
        }

        @Select("select * from biz_user_bank where user_id = #{userId} and audit_status = 2 order by id desc limit 1")
        AppUserBankRespVO getUserBank(@Param("userId") Long userId);

        @Select("select bank_account_name as bankAccountName, bank_account as bankAccount, bank_name as bankName " +
                        "from biz_publisher_info where tenant_id = #{tenantId} limit 1")
        AppProjectPaymentInfoRespVO getPublisherBankInfoByTenantId(@Param("tenantId") Long tenantId);

        @Select("select company_name as publisherCompanyName " +
                        "from biz_publisher_info where tenant_id = #{tenantId} limit 1")
        String getPublisherNameByTenantId(@Param("tenantId") Long tenantId);

        @Select("SELECT `name` from infra_file WHERE url = #{url}")
        String getProjectFileName(@Param("url") String url);

        @Select("SELECT " +
                        "   asset_type AS assetType, " + // 别名对应AssetTypeCount的字段
                        "   COUNT(*) AS count " +
                        "FROM " +
                        "   biz_project_info " + // 替换为你的实际表名
                        "WHERE " +
                        "   asset_type IS NOT NULL and sell_status = 1 and audit_status = 2 " + // 可选：排除空值
                        "GROUP BY " +
                        "   asset_type " +
                        "ORDER BY " +
                        "   asset_type ASC")
        List<AssetTypeCountRespVO> countProjectByAssetType();

        /**
         * 统计待上线审核的项目数量
         * 
         * @return 待上线审核项目数
         */
        default Long countOnlineAudit(){
                LambdaQueryWrapper<ProjectInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ProjectInfoDO::getAuditStatus, AuditStatusEnum.PENDING.getStatus());
                return selectCount(lambdaQueryWrapper);
        }

        /**
         * 统计待运行审核的项目数量
         * 
         * @return 待运行审核项目数
         */
        default Long countRunningAudit(){
                LambdaQueryWrapper<ProjectInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(ProjectInfoDO::getProjectStatus, RunStatusEnum.PENDING_RUN.getStatus());
                return selectCount(lambdaQueryWrapper);
        }

}
