package com.skm.exa.webapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@ApiModel(value = "问题管理添加VO")
public class QuestionBankSaveVO {
    @ApiModelProperty(value = "企业ID")
    private Long enterpriseId;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "技术类型 1编程，2算法，3数据库，4优化")
    @Range(min =  1, max = 4,message = "技术类型有误")
    private Long technologicalType;
    @ApiModelProperty(value = "题目类型 1问答，2单选 3多选，4编程")
    @Range(min =  1, max = 4 ,message = "题目类型有误")
    private Long topicType;
    @ApiModelProperty(value = "题目详情")
    private String topicDetails;
    @ApiModelProperty(value = "标签")
    private String label;
    @ApiModelProperty(value = "问题解答")
    private String answer;
    @ApiModelProperty(value = "状态 0 禁用 ：1 正常")
    private Long status;

    @ApiModelProperty(value = "选择题选项")
    private List<OptionCodesVo> optionCodesVoList ;
}
