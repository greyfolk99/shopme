package com.greyfolk99.shopme.controller.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class CookieHandler {
    private final Gson gson = new Gson();
    public <T> T decode(Cookie cookie, TypeToken<T> typeToken) {
        return gson.fromJson(URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8), typeToken.getType());
    }

    public <T> ResponseCookie encode(String name, T t, int expiredSecondsAfter){
        return ResponseCookie
            .from(name, URLEncoder.encode(gson.toJson(t), StandardCharsets.UTF_8))
            .maxAge(expiredSecondsAfter)
            .path("/")
            .sameSite("None")
            .secure(true).build();
    }
}
