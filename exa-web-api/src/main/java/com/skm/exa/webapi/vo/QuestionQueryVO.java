package com.skm.exa.webapi.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class QuestionQueryVO  {
    @ApiModelProperty(value = "技术类型")
    private Long technologicalType;
    @ApiModelProperty(value = "题目类型")
    private Long topicType;
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "", example = "2019-01-10")
    private Date startDt;
    @ApiModelProperty(value = "", example = "2019-5-28")
    private Date endDt;
}
