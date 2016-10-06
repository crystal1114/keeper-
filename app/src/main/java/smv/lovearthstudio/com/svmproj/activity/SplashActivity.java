package smv.lovearthstudio.com.svmproj.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.lovearthstudio.duasdk.Dua;
import com.lovearthstudio.duaui.DuaActivityLogin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import smv.lovearthstudio.com.svmproj.R;
import tyrantgit.widget.HeartLayout;

public class SplashActivity extends Activity {

    FrameLayout mFlSplash;

    private Random mRandom = new Random();
    private Timer mTimer = new Timer();
    private HeartLayout mHeartLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_splash);

        init();
    }

    private void init() {
        findView();
        initAndPlayAnimator();


    }

    private void findView() {
        mFlSplash = (FrameLayout) findViewById(R.id.fl_splash);

        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.addHeart(randomColor());
                    }
                });
            }
        }, 500, 200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    /**
     * 初始化动画,并播放动画
     */
    private void initAndPlayAnimator() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
        alphaAnimation.setDuration(6000);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                /**
                 * 动画播放结束后处理逻辑
                 * 进入Guide或main
                 */
                //   selectPager();
                enterMain();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFlSplash.startAnimation(alphaAnimation);
    }

    private void selectPager() {
        Dua.DuaUser duaUser = Dua.getInstance().getCurrentDuaUser();
        if (duaUser.logon) {
            enterMain();
        } else {
            startActivityForResult(new Intent(this, DuaActivityLogin.class), 10086);
        }
    }

    private void enterMain() {
        startActivity(new Intent(this, MainActivity.class));
        mTimer.cancel();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String operator) {
        if (operator.equals(DuaActivityLogin.LOGIN_SUCCESS)) {
            enterMain();
        }
    }
}
