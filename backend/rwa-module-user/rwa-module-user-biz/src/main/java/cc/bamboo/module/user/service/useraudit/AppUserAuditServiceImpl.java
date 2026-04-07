package cc.bamboo.module.user.service.useraudit;

import cc.bamboo.framework.common.util.object.BeanUtils;
import cc.bamboo.module.infra.api.file.FileApi;
import cc.bamboo.module.system.api.mail.MailSendApi;
import cc.bamboo.module.system.api.mail.dto.MailCodeUseReqDTO;
import cc.bamboo.module.system.api.sms.SmsCodeApi;
import cc.bamboo.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cc.bamboo.module.system.enums.sms.SmsSceneEnum;
import cc.bamboo.module.user.controller.app.useraudit.vo.*;

import cc.bamboo.module.user.dal.dataobject.useraudit.UserAuditDO;
import cc.bamboo.module.user.dal.dataobject.userbank.UserBankDO;
import cc.bamboo.module.user.dal.dataobject.userinfo.UserInfoDO;

import cc.bamboo.module.user.dal.mysql.useraudit.UserAuditMapper;
import cc.bamboo.module.user.dal.mysql.userbank.UserBankMapper;
import cc.bamboo.module.user.enums.UserAuditStatusEnum;
import cc.bamboo.module.user.service.userchain.UserChainService;
import cc.bamboo.module.user.service.userinfo.UserInfoService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cc.bamboo.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cc.bamboo.framework.common.util.servlet.ServletUtils.getClientIP;
import static cc.bamboo.module.project.enums.ErrorCodeConstants.INVALID_SMS_CODE;
import static cc.bamboo.module.user.enums.ErrorCodeConstants.*;

/**
 * APP端用户认证 Service 实现类
 *
 * @author Kiro
 */
@Service
@Validated
@Slf4j
public class AppUserAuditServiceImpl implements AppUserAuditService {

    @Resource
    private UserAuditMapper userAuditMapper;

    @Resource
    private UserBankMapper userBankMapper;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private FileApi fileApi;

    @Resource
    private MailSendApi mailSendApi;

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;

    @Resource
    private UserChainService userChainService;

    @Override
    public AppOcrIdCardRespVO ocrIdCard(AppOcrIdCardReqVO reqVO) throws IOException {
        // 1. 上传图片文件
        MultipartFile file = reqVO.getFile();
        String imageUrl = fileApi.createFile(file.getOriginalFilename(), "idcard",
                IoUtil.readBytes(file.getInputStream()));

        // 2. TODO: 集成OCR服务商API
        // 这里暂时返回空对象,等待OCR服务商确定后再实现
        log.warn("OCR识别功能暂未实现,请集成OCR服务商API. imageUrl={}, side={}", imageUrl, reqVO.getSide());

        return AppOcrIdCardRespVO.builder()
                .name("")
                .idCardNo("")
                .validDate("")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitAudit(Long userId, AppUserAuditSubmitReqVO reqVO) throws IOException {
        // 1. 校验用户是否存在
        UserInfoDO user = userInfoService.getInfo(userId);
        if (user == null) {
            throw exception(INFO_NOT_EXISTS);
        }

        // 2. 检查用户认证状态，如果已通过则不能再提交
        if (user.getAuditStatus() != null && user.getAuditStatus().equals(UserAuditStatusEnum.APPROVED.getStatus())) {
            throw exception(AUDIT_ALREADY_PASSED);
        }
        // 判断实名和银行卡名称是否一致
        if(!reqVO.getRealName().equals(reqVO.getBankAccountName())){
            throw exception(AUDIT_BANK_ACCOUNT_NAME_NOT_MATCH);
        }

        if(StringUtil.isNotBlank(reqVO.getEmailCode()) && StringUtil.isNotBlank(user.getEmail())){
            // 校验邮箱验证码
            MailCodeUseReqDTO mailCodeUseReq = new MailCodeUseReqDTO();
            mailCodeUseReq.setMail(user.getEmail());
            mailCodeUseReq.setScene(SmsSceneEnum.MEMBER_AUDIT.getScene());
            mailCodeUseReq.setCode(reqVO.getEmailCode());

            try {
                mailSendApi.verifyMailCode(mailCodeUseReq).checkError();
                log.info("[payOrder] 验证码验证成功，用户ID: {}, 邮箱: {}", userId, user.getEmail());
            } catch (Exception e) {
                log.error("[payOrder] 验证码验证失败，用户ID: {}, 邮箱: {}, 错误信息: {}",
                        userId, user.getMobile(), e.getMessage(), e);
                throw exception(INVALID_SMS_CODE);
            }
        }else{
            // 3. 校验短信验证码
            SmsCodeUseReqDTO smsCodeReq = new SmsCodeUseReqDTO();
            smsCodeReq.setMobile(user.getMobile());
            smsCodeReq.setCode(reqVO.getCode());
            smsCodeReq.setScene(SmsSceneEnum.MEMBER_AUDIT.getScene()); // 使用修改密码场景
            smsCodeReq.setUsedIp(getClientIP());
            smsCodeApi.useSmsCode(smsCodeReq).checkError();

        }


        // 3. 上传证件照片
        String idCardFrontUrl = reqVO.getIdCardFrontFile();
        String idCardBackUrl = reqVO.getIdCardBackFile();

        // 4. 上传其他证明材料
        List<String> investmentQualificationUrls = reqVO.getInvestmentQualificationFiles();
        List<String> bankFlowUrls = reqVO.getBankFlowFiles();
        List<String> residenceProofUrls = reqVO.getResidenceProofFiles();

        // 5. 查询当前用户最新的认证记录
        UserAuditDO latestAudit = userAuditMapper.selectOne(new LambdaQueryWrapper<UserAuditDO>()
                .eq(UserAuditDO::getUserId, userId)
                .eq(UserAuditDO::getIsLatest, true)
                .orderByDesc(UserAuditDO::getCreateTime)
                .last("LIMIT 1"));

        Long bankCardId;

        // 6. 如果最新记录是待审核且已有关联银行卡，则更新该银行卡信息
        if (latestAudit != null
                && UserAuditStatusEnum.PENDING.getStatus().equals(latestAudit.getAuditStatus())
                && latestAudit.getBankCardId() != null) {
            UserBankDO updateBank = new UserBankDO();
            updateBank.setId(latestAudit.getBankCardId());
            updateBank.setUserId(userId);
            updateBank.setBankAccount(reqVO.getBankAccount());
            updateBank.setBankAccountName(reqVO.getBankAccountName());
            updateBank.setBankName(reqVO.getBankName());

            userBankMapper.updateById(updateBank);
            bankCardId = latestAudit.getBankCardId();
        } else {
            // 7. 否则按原逻辑处理银行卡信息（查询或创建）
            bankCardId = saveBankCard(userId, reqVO);
        }

        // 8. 如果最新记录是待审核状态，则更新该记录，而不是新增
        if (latestAudit != null && UserAuditStatusEnum.PENDING.getStatus().equals(latestAudit.getAuditStatus())) {
            UserAuditDO updateObj = new UserAuditDO();
            updateObj.setId(latestAudit.getId());
            updateObj.setRealName(reqVO.getRealName());
            updateObj.setIdCard(reqVO.getIdCard());
            updateObj.setIdCardExpire(reqVO.getIdCardExpire());
            updateObj.setIdCardFrontUrl(idCardFrontUrl);
            updateObj.setIdCardBackUrl(idCardBackUrl);
            updateObj.setInvestmentQualificationUrl(listToJson(investmentQualificationUrls));
            updateObj.setBankFlowUrl(listToJson(bankFlowUrls));
            updateObj.setResidenceProofUrl(listToJson(residenceProofUrls));
            updateObj.setBankCardId(bankCardId);
            updateObj.setContactPhone(reqVO.getContactPhone());
            userAuditMapper.updateById(updateObj);
            return latestAudit.getId();
        }

        // 9. 如果不存在待审核记录，则创建新的认证记录
        int submitVersion = latestAudit != null && latestAudit.getSubmitVersion() != null
                ? latestAudit.getSubmitVersion() + 1
                : 1;

        userAuditMapper.update(null, new LambdaUpdateWrapper<UserAuditDO>()
                .eq(UserAuditDO::getUserId, userId)
                .eq(UserAuditDO::getIsLatest, true)
                .set(UserAuditDO::getIsLatest, false));

        UserAuditDO audit = UserAuditDO.builder()
                .userId(userId)
                .realName(reqVO.getRealName())
                .idCard(reqVO.getIdCard())
                .idCardExpire(reqVO.getIdCardExpire())
                .idCardFrontUrl(idCardFrontUrl)
                .idCardBackUrl(idCardBackUrl)
                .investmentQualificationUrl(listToJson(investmentQualificationUrls))
                .bankFlowUrl(listToJson(bankFlowUrls))
                .residenceProofUrl(listToJson(residenceProofUrls))
                .bankCardId(bankCardId)
                .contactPhone(reqVO.getContactPhone())
                .auditStatus(UserAuditStatusEnum.PENDING.getStatus())
                .submitVersion(submitVersion)
                .isLatest(true)
                .build();

        userAuditMapper.insert(audit);
        // 用户审核状态设置为审核中
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setId(userId);
        userInfoDO.setAuditStatus(UserAuditStatusEnum.PENDING.getStatus());
        userInfoService.updateInfo(userInfoDO);
        //创建链地址
        userChainService.createAddress(userId);
        return audit.getId();
    }

    @Override
    public AppUserAuditDetailRespVO getLatestAuditDetail(Long userId) {
        // 查询最新的认证记录
        UserAuditDO audit = userAuditMapper.selectOne(new LambdaQueryWrapper<UserAuditDO>()
                .eq(UserAuditDO::getUserId, userId)
                .eq(UserAuditDO::getIsLatest, true)
                .orderByDesc(UserAuditDO::getCreateTime)
                .last("LIMIT 1"));

        if (audit == null) {
            return null;
        }

        // 构建返回对象
        AppUserAuditDetailRespVO respVO = AppUserAuditDetailRespVO.builder()
                .id(audit.getId())
                .realName(audit.getRealName())
                .idCard(audit.getIdCard())
                .idCardExpire(audit.getIdCardExpire())
                .idCardFrontUrl(audit.getIdCardFrontUrl())
                .idCardBackUrl(audit.getIdCardBackUrl())
                .investmentQualificationUrls(jsonToList(audit.getInvestmentQualificationUrl()))
                .bankFlowUrls(jsonToList(audit.getBankFlowUrl()))
                .residenceProofUrls(jsonToList(audit.getResidenceProofUrl()))
                .email(audit.getEmail())
                .contactPhone(audit.getContactPhone())
                .auditStatus(audit.getAuditStatus())
                .auditStatusDesc(getAuditStatusDesc(audit.getAuditStatus()))
                .submitVersion(audit.getSubmitVersion())
                .auditRemark(audit.getAuditRemark())
                .createTime(audit.getCreateTime())
                .build();

        // 查询银行卡信息
        if (audit.getBankCardId() != null) {
            UserBankDO bankCard = userBankMapper.selectById(audit.getBankCardId());
            if (bankCard != null) {
                AppBankCardInfoRespVO cardInfoRespVO = BeanUtils.toBean(bankCard, AppBankCardInfoRespVO.class);
                // 如果不是审核不通过状态
                if (!UserAuditStatusEnum.REJECTED.getStatus().equals(audit.getAuditStatus())) {
                    respVO.setAuditRemark(null);
                }
                respVO.setBankCard(cardInfoRespVO);
            }
        }

        return respVO;
    }

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 文件URL
     */
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }
            // 判断文件大小
            if (file.getSize() > maxFileSize.toBytes()) {
                throw exception(FILE_SIZE_EXCEEDED);
            }

            return fileApi.createFile(file.getOriginalFilename(), IoUtil.readBytes(file.getInputStream()));
        } catch (IOException e) {
            throw exception(FILE_UPLOAD_ERROR);
        }

    }

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件URL列表
     */
    private List<String> uploadFiles(List<MultipartFile> files) throws IOException {
        if (CollUtil.isEmpty(files)) {
            return null;
        }
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String url = uploadFile(file);
                if (StrUtil.isNotBlank(url)) {
                    urls.add(url);
                }
            }
        }
        return urls.isEmpty() ? null : urls;
    }

    /**
     * 保存银行卡信息
     */
    private Long saveBankCard(Long userId, AppUserAuditSubmitReqVO reqVO) {
        // 查询是否已存在该银行卡
        UserBankDO existCard = userBankMapper.selectByUserIdAndCardNo(userId, reqVO.getBankAccount());
        if (existCard != null) {
            return existCard.getId();
        }

        // 创建新银行卡
        UserBankDO bankCard = UserBankDO.builder()
                .userId(userId)
                .bankAccount(reqVO.getBankAccount())
                .bankAccountName(reqVO.getBankAccountName())
                .bankName(reqVO.getBankName())
                .isDefault(false)
                .auditStatus(UserAuditStatusEnum.PENDING.getStatus())
                .build();

        userBankMapper.insert(bankCard);
        return bankCard.getId();
    }

    /**
     * List转JSON字符串
     */
    private String listToJson(List<String> list) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        return JSONUtil.toJsonStr(list);
    }

    /**
     * JSON字符串转List
     */
    private List<String> jsonToList(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return JSONUtil.toList(json, String.class);
        } catch (Exception e) {
            // 兼容旧数据,如果不是JSON格式,按逗号分隔
            return Arrays.asList(json.split(","));
        }
    }

    /**
     * 脱敏身份证号
     */
    private String maskIdCard(String idCard) {
        if (StrUtil.isBlank(idCard) || idCard.length() < 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 脱敏银行卡号
     */
    private String maskBankCardNo(String bankCardNo) {
        if (StrUtil.isBlank(bankCardNo) || bankCardNo.length() < 8) {
            return bankCardNo;
        }
        String first4 = bankCardNo.substring(0, 4);
        String last4 = bankCardNo.substring(bankCardNo.length() - 4);
        return first4 + " **** **** " + last4;
    }

    /**
     * 获取审核状态描述
     */
    private String getAuditStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0:
                return "待提交";
            case 1:
                return "待审核";
            case 2:
                return "审核通过";
            case 3:
                return "审核驳回";
            default:
                return "未知";
        }
    }

}
