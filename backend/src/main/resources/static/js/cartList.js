
    $(document).ready(function(){
        $("input[name=cartChkBox]").change( function(){
            getOrderTotalPrice();
        });
    });

    // 총 주문 금액 표시 변경
    function getOrderTotalPrice(){
        let orderTotalPrice = 0;
        $("input[name=cartChkBox]:checked").each(function() {
            const itemId = $(this).attr("data-item-id");
            const price = $("#price_" + itemId).attr("data-price");
            const count = $("#count_" + itemId).val();
            orderTotalPrice += price*count;
            console.log(orderTotalPrice)
        });
        $("#orderTotalPrice").html(
            orderTotalPrice.toLocaleString('ko-KR') + " 원");
    }

    // 전체선택 또는 전체해제
    function checkAll(){
        if ($("#checkall").prop("checked")) {
            $("input[name=cartChkBox]").prop("checked",true);}
        else {
            $("input[name=cartChkBox]").prop("checked",false);}
        getOrderTotalPrice();
    }

    // 상품 수량 표시 변경
    function changeCount(obj){
        const count = obj.value;
        const itemId = obj.id.split('_')[1];
        const price = $("#price_" + itemId).data("price");
        const totalPrice = count * price;
        $("#totalPrice_" + itemId).html(
            totalPrice.toLocaleString('ko-KR'));
        getOrderTotalPrice();
        updateCartItem(itemId, count);
        return { itemId, count }
    }

    // 상품 수량 업데이트 요청 전송
    function updateCartItem(itemId, count){
        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");
        const url = "/cart/api/item"
        const param = {
            itemId : itemId,
            count : count
        }
        $.ajax({
            url: url,
            type: "PATCH",
            data: JSON.stringify(param),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            cache: false,
            success: function (result, status) {
                console.log("cartItem count update success");},
            error: function (jqXHR, status, error) {
                console.log(status)
                if (jqXHR.status == 401) {
                    alert('로그인 후 이용해주세요');
                    location.href = '/auth/login';
                }
                else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        });
    }

    // 장바구니 삭제
    function deleteCartItem(obj) {

        const itemId = obj.id.split('_')[1];
        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");

        const url = "/cart/api/item" + "?itemId=" + itemId;
        if (!confirm("삭제하시겠습니까?")) { return; }
        $.ajax({
            url: url,
            type: "DELETE",
            beforeSend: function (xhr) {
                /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
                xhr.setRequestHeader(header, token);
            },
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            cache: false,
            success: function (result, status) {
                location.href = '/cart';
            },
            error: function (jqXHR, status, error) {
                if (jqXHR.status == 401) {
                    alert('로그인 후 이용해주세요');
                    location.href = '/auth/login';
                } else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        });
    }

    // 장바구니 상품(들) 주문
    function order() {
        const token = $("meta[name='_csrf']").attr("content");
        const header = $("meta[name='_csrf_header']").attr("content");
        const orderItemRequests = [];
        $("input[name=cartChkBox]:checked").each(function() {
            const itemId = $(this).attr("data-item-id");
            const count = $("#count_" + itemId).val();
            orderItemRequests.push({
                "itemId" : itemId,
                "count" : count
            });
        });
        const param = JSON.stringify(orderItemRequests);
        console.log(param);
        if (!confirm("주문하시겠습니까?")) { return; }
        $.ajax({
            url: "/order/api/cart",
            type: "POST",
            contentType: "application/json",
            data: param,
            beforeSend: function (xhr) {
                /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
                xhr.setRequestHeader(header, token);
            },
            cache: false,
            success: function (result, status) {
                alert("주문이 완료 되었습니다.");
                location.href = '/order/list';
            },
            error: function (jqXHR, status, error) {
                if(jqXHR.status == 401) {
                    alert('로그인 후 이용해주세요');
                    location.href = '/auth/login';
                } else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        });

    }
