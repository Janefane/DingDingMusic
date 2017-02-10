package com.dingmouren.dingdingmusic.ui.personal;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dingmouren.dingdingmusic.MyApplication;
import com.dingmouren.dingdingmusic.R;
import com.dingmouren.dingdingmusic.base.BaseActivity;
import com.dingmouren.dingdingmusic.ui.localmusic.LocalMusicActivity;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dingmouren on 2017/1/17.
 */

public class PersonalCenterActivity extends BaseActivity {
    private static final String TAG = PersonalCenterActivity.class.getName();
    @BindView(R.id.img_header) CircleImageView mImgHeader;
    @BindView(R.id.tv_username)TextView mUserName;
    @BindView(R.id.tv_local_music) TextView mLocalMusicCount;
    @BindView(R.id.tv_like) TextView mLikeMusic;
    @BindView(R.id.img_setting)ImageView mSetting;
    @BindView(R.id.container) LinearLayout mRootLayout;
    private Cursor mCursor;
    private long mCountLike;
    private int enterX;//传递过来的x坐标，是点击View的中心点的x坐标，揭露动画
    private int enterY;//传递过来的y坐标，是点击View的中心点的y坐标，揭露动画
    @Override
    public int setLayoutResourceID() {
        return R.layout.activity_personal;
    }

    @Override
    public void initView() {
        mCursor  = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (null != mCursor)mLocalMusicCount.setText("本地歌曲("+mCursor.getCount()+"首)");

        mCountLike  = MyApplication.getDaoSession().getLikeBeanDao().count();
        if (0 != mCountLike){
            mLikeMusic.setText("收藏歌曲("+mCountLike+"首)");
        }


        //揭露动画
        mRootLayout.post(new Runnable() {
            @Override
            public void run() {
                mRootLayout.setVisibility(View.VISIBLE);
                enterX = getIntent().getIntExtra("x",0);
                enterY = getIntent().getIntExtra("y",0);
                if (0 != enterX && 0 != enterY){
                    Animator animator = createRevealAnimator(false,enterX,enterY);
                    animator.start();
                }
            }
        });
    }




    @Override
    public void initData() {

    }

    @OnClick({R.id.card_local_music,R.id.card_like,R.id.img_setting})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.card_local_music:
                Intent intent = new Intent(PersonalCenterActivity.this,LocalMusicActivity.class);
                startActivity(intent);
                new Handler().postDelayed(()-> finish(),1000);
                break;
            case R.id.card_like:
                break;
            case R.id.img_setting:
                break;
        }
    }
    /**
     * 揭露动画
     */
    private Animator createRevealAnimator( boolean reversed,int x, int y) {
        float hypot = (float) Math.hypot(mRootLayout.getHeight(),mRootLayout.getWidth());
        float startRadius = reversed ? hypot : 0;
        float endRadius = reversed ? 0 : hypot;

        Animator animator = ViewAnimationUtils.createCircularReveal(mRootLayout,x,y,startRadius,endRadius);
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (reversed){
            animator.addListener(animatorListener);
        }
        return animator;
    }
    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mRootLayout.setVisibility(View.INVISIBLE);
            finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    @Override
    public void onBackPressed() {
        if (enterX != 0 && enterY != 0) {
            Animator animator = createRevealAnimator(true, enterX, enterY);
            animator.start();
        }else {
            super.onBackPressed();
        }
    }


}
