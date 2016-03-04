package com.example.jcman.btparadise.model;

import com.example.jcman.btparadise.util.StringUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcman on 16-3-3.
 */
public class Video {

    private String title;
    private String coverUrl;
    private String secondTitle;
    private String desc;
    private String time;
    private String link;

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    private String mark;

    public String getLink(){
        return link;
    }

    public void setLink(String link){
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSecondTitle() {
        return secondTitle;
    }

    public void setSecondTitle(String secondTitle) {
        this.secondTitle = secondTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static List<Video> getHotVideoList(Document doc){
        List<Video> _List = new ArrayList<>();
        if(doc!=null){
            return getHotOrNewVideoList(doc,"hotlst");
        }
        return _List;
    }

    public static List<Video> getNewVideoList(Document doc){
        List<Video> _List = new ArrayList<>();
        if(doc!=null){
            return getHotOrNewVideoList(doc,"nrlst");
        }
        return _List;
    }

    private static List<Video> getHotOrNewVideoList(Document doc,String s){
        List<Video> _List = new ArrayList<>();
        Elements es = doc.getElementById(s).getElementsByTag("li");
        for(Element e:es){
            String link = e.getElementsByAttributeValue("class","img").first().getElementsByTag("a").first().attr("href");
            link = StringUtil.WEB_MAIN_PAGE_URL+link;
            Element img = e.getElementsByAttributeValue("class","img").first().getElementsByTag("img").first();
            String coverUrl = img.attr("src");
            String mark = e.getElementsByAttributeValue("class","tit").first().text();
            String title = e.getElementsByAttributeValue("class","tit").first().getElementsByTag("a").first().text();
            mark = mark.substring(mark.length()-3);

            Video video = new Video();
            video.setLink(link);
            video.setCoverUrl(coverUrl);
            video.setTitle(title);
            video.setMark(mark);
            _List.add(video);
        }
        return _List;
    }
}
