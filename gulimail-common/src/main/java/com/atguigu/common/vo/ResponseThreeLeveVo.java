package com.atguigu.common.vo;


import lombok.Data;

import java.util.List;

/**
 * 返回三级分类数据
 */
@Data
public class ResponseThreeLeveVo {

    private String catalog1Id;
    private String catalog1Name;
    private List<ResponseCategoryLog2Vo> responseCategoryLog2VoList;

}
