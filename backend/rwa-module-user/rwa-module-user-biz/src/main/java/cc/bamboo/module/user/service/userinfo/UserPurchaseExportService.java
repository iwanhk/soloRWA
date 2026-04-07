package cc.bamboo.module.user.service.userinfo;

import cc.bamboo.module.user.controller.admin.userinfo.vo.UserInfoPageReqVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UserPurchaseExportService {

    void exportUserPurchaseExcel(UserInfoPageReqVO pageReqVO, List<Long> userIds, HttpServletResponse response)
            throws IOException;
}

