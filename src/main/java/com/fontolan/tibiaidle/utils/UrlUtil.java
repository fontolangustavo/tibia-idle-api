package com.fontolan.tibiaidle.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class UrlUtil {
    public static String buildUrlForImage(String relativePath) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + "/api/v1/public/images/" + relativePath;
    }
}
