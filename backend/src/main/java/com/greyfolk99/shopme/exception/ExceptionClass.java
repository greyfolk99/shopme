package com.greyfolk99.shopme.exception;

public enum ExceptionClass {
    GENERAL("서버"),
    CART("장바구니"),
    CART_ITEM("장바구니 상품"),
    ITEM("상품"),
    ITEM_IMG("상품 이미지"),
    MEMBER("고객"),
    ORDER("주문"),
    ORDER_ITEM("주문 상품"),
    FILE("파일")
    ;
    private final String exceptionClass;
    ExceptionClass(String exceptionClass){ this.exceptionClass = exceptionClass; }
    public String getExceptionClass(){ return exceptionClass; }
    @Override
    public String toString(){ return getExceptionClass() + "에 문제가 있습니다. "; }
}
