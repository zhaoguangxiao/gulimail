package com.atguigu.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCategoryLog2Vo {

    private String catalog1Id; //1级父分类id
    private List<CategoryLog3Vo> catalog3List;//三级子分类集合
    private String id;
    private String name;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static  class CategoryLog3Vo{
        private String catalog2Id;
        private String id;
        private String name;

    }


}
