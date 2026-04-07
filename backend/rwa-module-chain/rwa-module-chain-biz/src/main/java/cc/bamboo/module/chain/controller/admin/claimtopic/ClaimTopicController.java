package cc.bamboo.module.chain.controller.admin.claimtopic;

import cc.bamboo.framework.common.pojo.CommonResult;
import cc.bamboo.module.chain.controller.admin.claimtopic.vo.CreateClaimTopicReqVO;
import cc.bamboo.module.chain.dal.dataobject.claimtopics.ClaimTopicsDO;
import cc.bamboo.module.chain.service.claimtopic.ClaimTopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static cc.bamboo.framework.common.pojo.CommonResult.success;

/**
 * ClaimTopic 管理 Controller
 * 
 * @author Swolf
 */
@Tag(name = "管理后台 - ClaimTopic 管理")
@RestController
@RequestMapping("/chain/claim-topics")
@Validated
public class ClaimTopicController {
    
    @Resource
    private ClaimTopicService claimTopicService;
    
    @GetMapping
    @Operation(summary = "获取所有 ClaimTopic")
    public CommonResult<List<ClaimTopicsDO>> getAllClaimTopics() {
        return success(claimTopicService.getAllClaimTopics());
    }
    
    @PostMapping
    @Operation(summary = "创建 ClaimTopic")
    public CommonResult<ClaimTopicsDO> createClaimTopic(@RequestBody @Validated CreateClaimTopicReqVO reqVO) {
        return success(claimTopicService.createClaimTopic(reqVO.getName(), reqVO.getValue()));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 ClaimTopic")
    public CommonResult<ClaimTopicsDO> getClaimTopicById(@PathVariable Long id) {
        return success(claimTopicService.getClaimTopicById(id));
    }
}
