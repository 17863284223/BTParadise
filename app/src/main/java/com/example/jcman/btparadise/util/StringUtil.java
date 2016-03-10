package com.example.jcman.btparadise.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by jcman on 16-3-3.
 */
public class StringUtil {

    public static final String WEB_MAIN_PAGE_URL = "http://www.bttiantang.cc";

    public static String getSearchVideoUrl(String videoName){
        String url = "http://www.bttiantang.cc/s.php?sitesearch=www.bttiantang.com&domains=bttiantang.com&hl=zh-CN&ie=UTF-8&oe=UTF-8&q=";
        try {
            videoName = URLEncoder.encode(videoName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url+=videoName;
        return url;
    }
}
