package com.example.jcman.btparadise.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jcman on 16-3-10.
 */
public class VideoType {

    private String typename;
    private String link;


    public static List<VideoType> getVideoTypes(){

        String linkBase = "http://www.bttiantang.cc/movie.php?/type,order/";
        List<String> types = Arrays.asList("剧情","喜剧","惊悚","动作","爱情","犯罪","恐怖","冒险","科幻",
                "悬疑","奇幻","动画","战争","历史","古装","情色");
        List<String> links = Arrays.asList("1","7","27","18","2","8","10","14","20","21","3","13","49","29","61","126");
        List<VideoType> list = new ArrayList<>();
        for(int i=0;i<types.size();i++){
            VideoType type = new VideoType();
            type.setTypename(types.get(i));
            type.setLink(linkBase+links.get(i)+",update");
            list.add(type);
        }
        return list;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
