package com.atguigu.gulimail.order.exception;

public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException(Long skuId) {
        super(skuId + "当前库存不足,没有足够的库存");
    }


    public NoStockException(String message) {
        super(message);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
