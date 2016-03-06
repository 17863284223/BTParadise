package com.example.jcman.btparadise;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.tool.logger.Logger;
import com.example.jcman.btparadise.activity.VideoDetailActivity;
import com.example.jcman.btparadise.adapter.CommonAdapter;
import com.example.jcman.btparadise.adapter.MyAnimatorListenerAdapter;
import com.example.jcman.btparadise.adapter.MyViewPagerAdapter;
import com.example.jcman.btparadise.adapter.ViewHolder;
import com.example.jcman.btparadise.model.Video;
import com.example.jcman.btparadise.util.ScreenUtil;
import com.example.jcman.btparadise.util.StringUtil;
import com.example.jcman.btparadise.util.VersionUtil;
import com.example.jcman.btparadise.view.PTRListView;
import com.example.jcman.btparadise.view.PullToRefreshLayout;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.arcanimator.ArcAnimator;
import io.codetail.arcanimator.Side;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    private Document mDoc;
    private List<Video> mVideos;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private CardView mCardView;
    private ImageView iv_bottom_search;
    private EditText edit_text_search;
    private View view_hide;
    private Toolbar toolbar;

    private MyViewPagerAdapter mAdapter;
    private List<View> mViews;
    private List<String> mTitles;
    FloatingActionButton mFab;
    private SupportAnimator mAnimator;
    private MyListViewAdapter mPTRAdapter;
    private List<Video> mPTRVideoList;
    PullToRefreshLayout refreshLayout;

    private int mPagePos = 0;
    private int mMaxPage = 1;
    private int mCurrentVideoPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDataFromIntent();
        initView();
        initPageViews();
        initListeners();
        bit_loading = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_cover_loading);
        mAdapter = new MyViewPagerAdapter(this,mViews,mTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        handFabPathAndSearch();
        mMaxPage = getMaxPage(mDoc);
    }

    private void initListeners() {
        mFab.setOnClickListener(this);
        view_hide.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==2){
                    mFab.setImageResource(R.mipmap.ic_cate);
                }else{
                    mFab.setImageResource(R.mipmap.ic_search);
                }
                mPagePos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPageViews(){
        mTitles = Arrays.asList("火热下载","新片推荐","电影检索");
        mViews = new ArrayList<>();
        View v_hot = View.inflate(this,R.layout.v_hot_video,null);
        initHotVideoView(v_hot);
        mViews.add(v_hot);
        View v_new = View.inflate(this,R.layout.v_hot_video,null);
        initNewVideoView(v_new);
        mViews.add(v_new);
        View v_search = View.inflate(this,R.layout.v_main_video,null);
        initMainVideoView(v_search);
        mViews.add(v_search);
    }

    private void initMainVideoView(View v_search){

        PTRListView ptrListView = (PTRListView) v_search.findViewById(R.id.ptr_listview_ac_main);
        refreshLayout = (PullToRefreshLayout) v_search.findViewById(R.id.ptrLayout_ac_main);
        mPTRVideoList = Video.getMainVideoList(mDoc);
        mPTRAdapter = new MyListViewAdapter(this,mPTRVideoList,R.layout.item_hot_video);
        ptrListView.setAdapter(mPTRAdapter);
        ptrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,VideoDetailActivity.class);
                intent.putExtra("video",mPTRVideoList.get(position));
                startActivity(intent);
            }
        });
        refreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(mCurrentVideoPage+1<=mMaxPage){
                                mCurrentVideoPage++;
                                Document doc = Jsoup.connect(getNextPageUrl()).get();
                                List<Video> _List = Video.getMainVideoList(doc);
                                for(Video video:_List)
                                    mPTRVideoList.add(video);
                                notifyDataChanged();
                            }else{
                                loadFail();
                                showMessage("没有更多了");
                            }
                        } catch (IOException e) {
                            mCurrentVideoPage--;
                            loadFail();
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private void loadFail(){
        runOnUiThread(new Runnable() {
            @Override
            public void run(){
                refreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
            }
        });
    }

    private void notifyDataChanged(){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                refreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                mPTRAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showMessage(final String msg){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getNextPageUrl(){
        String url = StringUtil.WEB_MAIN_PAGE_URL+"/?PageNo=";
        url+=mCurrentVideoPage;
        return url;
    }

    private void initNewVideoView(View v){
        final List<Video> list = Video.getNewVideoList(mDoc);
        ListView listView = (ListView) v.findViewById(R.id.listview_hot_video);
        listView.setAdapter(new MyListViewAdapter(this,list,R.layout.item_hot_video));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                intent.putExtra("video",list.get(position));
                startActivity(intent);
            }
        });
    }

    private void initHotVideoView(View v) {
        ListView listView = (ListView) v.findViewById(R.id.listview_hot_video);
        mVideos = getHotVideoList();
        listView.setAdapter(new MyListViewAdapter(this,mVideos,R.layout.item_hot_video));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                intent.putExtra("video",mVideos.get(position));
                startActivity(intent);
            }
        });
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BT天堂");
        toolbar.setLogo(R.mipmap.ic_icon);
        setSupportActionBar(toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.tab_ac_main);
        mViewPager = (ViewPager) findViewById(R.id.vp_ac_main);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mCardView = (CardView) findViewById(R.id.card_search);
        iv_bottom_search = (ImageView) findViewById(R.id.iv_bottom_search);
        edit_text_search = (EditText) findViewById(R.id.edit_text_search);
        view_hide = findViewById(R.id.view_hide);


    }

    private void getDataFromIntent(){
        mDoc = Jsoup.parse(getIntent().getStringExtra("web"));
    }

    private void handFabPathAndSearch(){
        iv_bottom_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (mAnimator != null && !mAnimator.isRunning()){
                    mAnimator = mAnimator.reverse();
                    float curTranslationX = iv_bottom_search.getTranslationX();
                    final ObjectAnimator animator = ObjectAnimator.ofFloat(iv_bottom_search, "translationX", curTranslationX, 0);
                    animator.setDuration(600);
                    mAnimator.addListener(new MyAnimatorListenerAdapter(){
                        @Override
                        public void onAnimationStart(){
                            animator.start();
                        }

                        @Override
                        public void onAnimationEnd(){
                            mAnimator = null;
                            mFab.setVisibility(View.VISIBLE);
                            mCardView.setVisibility(View.GONE);
                            if (VersionUtil.checkVersionIntMoreThan19()){
                                ArcAnimator.createArcAnimator(mFab, ScreenUtil.getScreenWidth(MainActivity.this) - mFab.getWidth() / 2 - ScreenUtil.dip2px(16,MainActivity.this), ScreenUtil.getScreenHeight(MainActivity.this) - mFab.getHeight() - ScreenUtil.dip2px(16,MainActivity.this), 45.0f, Side.LEFT)
                                        .setDuration(500)
                                        .start();
                            } else {
                                ArcAnimator.createArcAnimator(mFab, ScreenUtil.getScreenWidth(MainActivity.this) - mFab.getWidth() / 2 - ScreenUtil.dip2px(16,MainActivity.this), ScreenUtil.getScreenHeight(MainActivity.this) - mFab.getHeight() / 2 - ScreenUtil.dip2px(16,MainActivity.this), 45.0f, Side.LEFT)
                                        .setDuration(500)
                                        .start();
                            }
                            view_hide.setVisibility(View.GONE);
                            new Handler().postDelayed(new Runnable(){
                                @Override
                                public void run(){
                                    searchMovie();
                                }
                            }, 500);
                        }
                    });
                } else if (mAnimator != null) {
                    mAnimator.cancel();
                    return;
                } else {
                    int cx = mCardView.getRight();
                    int cy = mCardView.getBottom();
                    float curTranslationX = iv_bottom_search.getTranslationX();
                    final ObjectAnimator animator = ObjectAnimator.ofFloat(iv_bottom_search, "translationX", curTranslationX, cx / 2 - ScreenUtil.dip2px(24,MainActivity.this));
                    animator.setDuration(600);
                    float radius = r(mCardView.getWidth(), mCardView.getHeight());
                    mAnimator = ViewAnimationUtils.createCircularReveal(mCardView, cx / 2, cy - ScreenUtil.dip2px(32,MainActivity.this), ScreenUtil.dip2px(20,MainActivity.this), radius);
                    mAnimator.addListener(new MyAnimatorListenerAdapter(){
                        @Override
                        public void onAnimationStart(){
                            animator.start();
                        }
                    });
                }
                mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimator.setDuration(600);
                mAnimator.start();
            }
        });
    }

    private void searchMovie(){
        String str = edit_text_search.getText().toString();
        if(str.length()>0){
            edit_text_search.setText("");
            Toast.makeText(this, str,Toast.LENGTH_SHORT).show();
        }
    }

    static float r(int a, int b){
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    private List<Video> getHotVideoList(){
        List<Video> _List = new ArrayList<>();
        if(mDoc!=null){
            _List = Video.getHotVideoList(mDoc);
        }
        return _List;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            // 先隐藏搜索框
            showCloseDialog();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void showCloseDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("确认要退出BT天堂吗?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
//            Toast.makeText(this,"search",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Bitmap bit_loading;

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.fab){
            if(mPagePos!=2)
                clickOnFab();
        }else if(v.getId()==R.id.view_hide){
            if(mAnimator!=null&&!mAnimator.isRunning())
                clickOnViewHide();
        }
    }

    private void clickOnViewHide(){
        edit_text_search.setText("");
        iv_bottom_search.performClick();
    }

    private void clickOnFab() {
        view_hide.setVisibility(View.VISIBLE);
        ArcAnimator.createArcAnimator(mFab, ScreenUtil.getScreenWidth(this)/2, ScreenUtil.getStatusHeight(this)+(toolbar.getHeight()/2), 45.0f, Side.LEFT)
                .setDuration(500)
                .start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCardView.setVisibility(View.VISIBLE);
                mFab.setVisibility(View.GONE);
                iv_bottom_search.performClick();
            }
        }, 600);
    }

    private int getMaxPage(Document doc){
        Element e = doc.getElementsByAttributeValue("class","pagelist").
                first().getElementsByAttributeValue("class","pageinfo").first().getElementsByTag("strong").first();
        String num = e.text();
        return Integer.parseInt(num);
    }

    private class MyListViewAdapter extends CommonAdapter<Video>{

        public MyListViewAdapter(Context context, List<Video> list, int layoutId){
            super(context, list, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, Video video){
            holder.display(MainActivity.this,R.id.iv_item_hot_video,video.getCoverUrl(),bit_loading);
            holder.setText(R.id.tv_item_hot_video_title,video.getTitle());
            holder.setText(R.id.tv_item_hot_video_mark,video.getMark());
        }
    }
}
