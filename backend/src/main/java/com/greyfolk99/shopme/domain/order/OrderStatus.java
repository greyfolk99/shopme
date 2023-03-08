package com.greyfolk99.shopme.domain.order;

public enum OrderStatus {
    // 정상 프로세스
    CONFIRMED, // 주문 완료
    SHIPPED, // 발송 완료
    DELIVERED, // 배송 완료

    // 그 외
    PENDING, // 주문 전 수동으로 검사해야할 때
    BACKORDERED, // 예약 주문
    CANCELLED, // 취소됨
    REFUNDED, // 환불됨
    FAILED, // 결제 실패
    ONHOLD, // 결제 요청 대기중
    ;
}
