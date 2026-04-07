package cc.bamboo.module.user.service.useraudit;

import cc.bamboo.module.user.controller.app.useraudit.vo.AppOcrIdCardReqVO;
import cc.bamboo.module.user.controller.app.useraudit.vo.AppOcrIdCardRespVO;
import cc.bamboo.module.user.controller.app.useraudit.vo.AppUserAuditDetailRespVO;
import cc.bamboo.module.user.controller.app.useraudit.vo.AppUserAuditSubmitReqVO;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

/**
 * APP端用户认证 Service 接口
 *
 * @author Kiro
 */
public interface AppUserAuditService {

    /**
     * OCR识别证件信息
     *
     * @param reqVO 识别请求
     * @return 识别结果
     * @throws Exception 文件处理异常
     */
    AppOcrIdCardRespVO ocrIdCard(@Valid AppOcrIdCardReqVO reqVO) throws IOException;

    /**
     * 提交用户认证
     *
     * @param userId 用户ID
     * @param reqVO 认证信息
     * @return 审核记录ID
     * @throws Exception 文件处理异常
     */
    Long submitAudit(Long userId, @Valid AppUserAuditSubmitReqVO reqVO) throws IOException;

    /**
     * 获取用户最新的认证详情
     *
     * @param userId 用户ID
     * @return 认证详情
     */
    AppUserAuditDetailRespVO getLatestAuditDetail(Long userId);

     /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 文件URL
     */
    String uploadFile(MultipartFile file);

}
