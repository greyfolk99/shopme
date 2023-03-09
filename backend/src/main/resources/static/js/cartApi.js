const cartApi = () => {

    let cartItems = [];
    let message = "";
    let redirectUrl = "/";

    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    const contentType = "application/json; charset=utf-8"
    const dataType = "json"

    /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
    const setCsrfToken = function (xhr) { xhr.setRequestHeader( token , header ) };

    const get = (isUser) => {
        $.ajax({
            type: "GET",
            url: "/cart/api/list",
            contentType: contentType,
            dataType: dataType,
            cache: false,
            beforeSend: setCsrfToken,
            success: function (data) {
                cartItems = data;
            },
            error: function (jqXHR, status, error) {
                console.log(status)
                if (jqXHR.status === 401) {
                    alert('로그인 후 이용해주세요');
                    location.href = "/auth/login";
                } else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        })
    }

    const post = (itemId, count) => {
        $.ajax({
            type: "POST",
            url: "/cart/api/item",
            data: { itemId: itemId, count: count },
            contentType: contentType,
            dataType: dataType,
            cache: false,
            beforeSend: setCsrfToken,
            success: function (result, status) {
                console.log(status)
                alert("상품을 장바구니에 담았습니다.");
                location.href = "/";
            },
            error: function (jqXHR, status, error) {
                console.log(status)
                if (jqXHR.status === 401) {
                    alert('로그인 후 이용해주세요');
                    location.href = "/auth/login";
                } else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        });
    };

    const patch = (itemId, count) => {
        $.ajax({
            type: "PATCH",
            url: "/cart/api/item",
            data: { itemId : itemId, count : count },
            contentType: contentType,
            dataType: dataType,
            cache: false,
            beforeSend: function (xhr) {
                /* 데이터를 전송하기 전에 헤더에 csrf값을 설정 */
                xhr.setRequestHeader(header, token);
            },
            success: function (result, status) {
                console.log("cartItem count update success");},
            error: function (jqXHR, status, error) {
                console.log(status)
                if (jqXHR.status === 401) {
                    alert('로그인 후 이용해주세요');
                    location.href = "/auth/login";
                }
                else {
                    alert(jqXHR.responseJSON.message);
                }

            }
        });
    }
    const deleteItem = (itemId) => {
        $.ajax({
            type: "DELETE",
            url: "/cart/api/item",
            data: { itemId: itemId },
            contentType: contentType,
            dataType: dataType,
            cache: false,
            beforeSend: setCsrfToken,
            success: function (data, status) {
                location.href = "/cart";
            },
            error: function (jqXHR, status, error) {
                if (jqXHR.status === 401) {
                    alert("로그인 후 이용해주세요");
                    location.href = "/auth/login";
                } else {
                    alert(jqXHR.responseJSON.message);
                }
            }
        });
    }
    return {
        get: cartItems,
        add: post,
        delete: deleteItem,
        update: patch,
        message: message,
        redirect: redirectUrl,
    }
}

