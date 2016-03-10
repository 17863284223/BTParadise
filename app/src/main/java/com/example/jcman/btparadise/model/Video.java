package com.example.jcman.btparadise.model;

import com.app.tool.logger.Logger;
import com.example.jcman.btparadise.util.StringUtil;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jcman on 16-3-3.
 */
public class Video implements Serializable{

    private String title;
    private String coverUrl;
    private String secondTitle;
    private String desc;
    private String time;
    private String link;
    private String label;
    private String locality;
    private String daoyan;
    private String actor;
    private String size;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLabel(){
        return label;
    }

    public void setLabel(String label){
        this.label = label;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDaoyan() {
        return daoyan;
    }

    public void setDaoyan(String daoyan) {
        this.daoyan = daoyan;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

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


    @Override
    public String toString() {
        return "Video{" +
                "title='" + title + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", secondTitle='" + secondTitle + '\'' +
                ", desc='" + desc + '\'' +
                ", time='" + time + '\'' +
                ", link='" + link + '\'' +
                ", mark='" + mark + '\'' +
                '}';
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

    public static List<Video> getMainVideoList(Document doc){
        List<Video> _List = new ArrayList<>();
        if(doc!=null){
            Elements es = doc.getElementsByAttributeValue("class","item cl");
            for(Element e:es){
                try {
                    String title = e.getElementsByAttributeValue("class","tt cl").first().getElementsByTag("b").first().text();
                    String link = e.getElementsByAttributeValue("class","tt cl").first().getElementsByTag("a").first().attr("href");
                    link = StringUtil.WEB_MAIN_PAGE_URL+link;
                    String mark = e.getElementsByAttributeValue("class","rt").first().getElementsByTag("strong").first().text();
                    String coverUrl = e.getElementsByAttributeValue("class","litpic").first().getElementsByTag("img").first().attr("src");
                    Video video = new Video();
                    video.setMark(mark);
                    video.setTitle(title);
                    video.setCoverUrl(coverUrl);
                    video.setLink(link);
                    _List.add(video);
                }catch (Exception e1){
                    e1.printStackTrace();
                }

            }
        }
        return _List;
    }

    public static Video getVideoInfo(Document doc){
        Video video = new Video();
        Element e_parent = doc.getElementsByAttributeValue("class","moviedteail").first();
        String coverUrl = e_parent.getElementsByAttributeValue("class","moviedteail").first().getElementsByTag("img").first().attr("src");
        String title = e_parent.getElementsByAttributeValue("class","moviedteail_tt").first().getElementsByTag("h1").first().text();
        Elements es = e_parent.getElementsByAttributeValue("class","moviedteail_list").first().getElementsByTag("li");
        String label = es.get(0).text();
        String locality = es.get(1).text();
        String time = es.get(2).text();
        String daoyan = es.get(3).text();
        String actor = es.get(4).text();
        video.setTitle(title);
        video.setCoverUrl(coverUrl);
        video.setActor(actor);
        video.setLabel(label);
        video.setTime(time);
        video.setLocality(locality);
        video.setDaoyan(daoyan);
        return video;
    }


    public static List<Video> getVideoDownList(Document doc){
        List<Video> _List = new ArrayList<>();
        Elements es = doc.getElementsByAttributeValue("class","tinfo");
        for(Element e:es){
            String size = e.getElementsByTag("a").first().getElementsByTag("p").first().getElementsByTag("em").first().text();
            String title = e.getElementsByTag("a").first().attr("title");
            String link = e.getElementsByTag("a").attr("href");
            Video video = new Video();
            video.setTitle(title);
            link = StringUtil.WEB_MAIN_PAGE_URL+link;
            video.setLink(link);
            video.setSize(size);
            Pattern pattern = Pattern.compile("【.+】");
            Matcher matcher = pattern.matcher(title);
            while (matcher.find())
                title = matcher.group();
            title+=size;
            video.setTitle(title);
            _List.add(video);
        }
        return _List;
    }

    public static List<Video> getVideoList(Document doc){
        List<Video> _List = new ArrayList<>();
        Elements es = doc.getElementsByAttributeValue("class","item cl");
        for(int i=0;i<es.size()-1;i++){
            Element e = es.get(i);
            String title = e.getElementsByAttributeValue("class","tt cl").first().getElementsByTag("b").first().text();
            String link = e.getElementsByAttributeValue("class","tt cl").first().getElementsByTag("a").first().attr("href");
            link = StringUtil.WEB_MAIN_PAGE_URL+link;
            Logger.e(link);
            String mark = e.getElementsByAttributeValue("class","rt").first().text().replace("豆瓣评分：","");
            String coverUrl = e.getElementsByAttributeValue("class","litpic").first().getElementsByTag("img").first().attr("src");
            Video video = new Video();
            video.setCoverUrl(coverUrl);
            video.setTitle(title);
            video.setLink(link);
            video.setMark(mark);
            _List.add(video);
        }
        return _List;
    }
}
