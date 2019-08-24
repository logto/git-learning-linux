one
two
import android.app.Activity;
555555
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.widget.Toast;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
666666
import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.systemui.R;
import com.mstar.android.tv.TvCommonManager;

public class NavigationBar implements View.OnTouchListener,View.OnClickListener{
    private Context context;
    private View mBarPanel;
    private LinearLayout mbar_content;

    private int windowHeight;
    private int windowWidth;

    private WindowManager baseWindowManager;
    private WindowManager.LayoutParams layoutnavigationbar;

    private final int SIDESLIP_BAR_TIMEOUT = 0;
    private final int SIDESLIP_BAR_CANCEL = 1;
    private final int SIDESLIP_BAR_TIME = 2;
    private final int SIDESLIP_BAR_OUTTIME_CANCEL = 10000;

    private WindowManager.LayoutParams layoutnavigationbarleft;
    private WindowManager.LayoutParams layoutnavigationbarright;
    private View mlayoutleft;
    private LinearLayout mlayoutleftll;
    private View mlayoutright;
    private LinearLayout mlayoutrightll;
    private Boolean startview = false;
    //private float rawx;
    private boolean isrileft;
    private ImageButton return_bt,home_bt,rerecent_bt,folder_bt,brush_bt,source_bt,setting_bt;
    public WindowManager.LayoutParams params;
    private float lastY;
    private float mTouchStartY;
    private Boolean isNavOnDown;
	private boolean isOnNavigation;
	private NavigationStateChangeRecevier mNavigationStateChangeRecevier;
	private boolean mReceiverRegiste = false;
	private static final String ON_NAVIGATION_CHANGED_ACTION ="android.intent.action.ON_NAVIGATION_CHANGED";
	private static final String ON_SLIDE_NAVIGATION_CHANGED_ACTION ="android.intent.action.ON_SLIDE_NAVIGATION_CHANGED";


   public NavigationBar (Context context){
        this.context = context;
        baseWindowManager = (WindowManager)context.getSystemService(context.WINDOW_SERVICE);
        initview();
        initwindows();
    }

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SIDESLIP_BAR_TIMEOUT:
                        removeview();
                        break;

                    case SIDESLIP_BAR_CANCEL:

                        break;
                    case SIDESLIP_BAR_TIME :

                        break;
                    default:
                        break;
                }
            }
        };

        public void initview(){
        windowHeight = baseWindowManager.getDefaultDisplay().getHeight();
        windowWidth = baseWindowManager.getDefaultDisplay().getWidth();
        mBarPanel = LayoutInflater.from(context).inflate(R.layout.activity_navigation,null);
//        mBarPanel = View.inflate(context. R.layout.activity_navigation, null);
        mbar_content = (LinearLayout) mBarPanel.findViewById(R.id.navigation_content_rl);

        initImageButton();
        mBarPanel.setOnTouchListener(this);
        mBarPanel.setOnClickListener(this);
		//register broadreceiver
		mNavigationStateChangeRecevier = new NavigationStateChangeRecevier();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ON_NAVIGATION_CHANGED_ACTION);
		context.registerReceiver(mNavigationStateChangeRecevier, intentFilter);
		mReceiverRegiste = true;
		
    }

    private void initImageButton(){
        return_bt = (ImageButton) mBarPanel.findViewById(R.id.navigation_back);
        home_bt = (ImageButton) mBarPanel.findViewById(R.id.navigation_home);
        rerecent_bt = (ImageButton) mBarPanel.findViewById(R.id.navigation_recent);
        folder_bt = (ImageButton) mBarPanel.findViewById(R.id.navigation_folder);
        brush_bt = (ImageButton) mBarPanel.findViewById(R.id.navigation_brush);
        source_bt = (ImageButton) mBarPanel.findViewById(R.id.navigation_source);
        setting_bt = (ImageButton) mBarPanel.findViewById(R.id.navigation_setting);

        return_bt.setOnTouchListener(this);
        home_bt.setOnTouchListener(this);
        rerecent_bt.setOnTouchListener(this);
        folder_bt.setOnTouchListener(this);
        brush_bt.setOnTouchListener(this);
        source_bt.setOnTouchListener(this);
        setting_bt.setOnTouchListener(this);

        return_bt.setOnClickListener(this);
        home_bt.setOnClickListener(this);
        rerecent_bt.setOnClickListener(this);
        folder_bt.setOnClickListener(this);
        brush_bt.setOnClickListener(this);
        source_bt.setOnClickListener(this);
        setting_bt.setOnClickListener(this);

    }

    private void initwindows(){
        layoutnavigationbar = new WindowManager.LayoutParams(
                windowWidth/20,windowHeight*18/32,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
                );
        layoutnavigationbarleft = new WindowManager.LayoutParams(
                29,  54,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSLUCENT);
        layoutnavigationbarleft.gravity = Gravity.LEFT;


        layoutnavigationbarright = new WindowManager.LayoutParams(
        		29 ,  54,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING |
                WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
                PixelFormat.TRANSLUCENT);
        layoutnavigationbarright.gravity = Gravity.RIGHT;
        mlayoutleft = LayoutInflater.from(context).inflate(R.layout.nav_bar_left, null);
        mlayoutleftll =(LinearLayout) mlayoutleft.findViewById(R.id.nav_bar_left);

        mlayoutright = LayoutInflater.from(context).inflate(R.layout.nav_bar_right, null);
        mlayoutrightll =(LinearLayout) mlayoutright.findViewById(R.id.nav_bar_right);

        mlayoutleftll.setOnTouchListener(this);
        mlayoutrightll.setOnTouchListener(this);

        mlayoutleftll.setOnClickListener(this);
        mlayoutrightll.setOnClickListener(this);

        baseWindowManager.addView(mlayoutleft,layoutnavigationbarleft);
        baseWindowManager.addView(mlayoutright,layoutnavigationbarright);

		isOnNavigation = Settings.Global.getInt(context.getContentResolver(),Settings.Global.NAVIGATION_ON, 0) != 0;
		updateNavigationState();
	}
	
	private void updateNavigationState(){
		if(isOnNavigation) {
			mlayoutleft.setVisibility(View.VISIBLE);
			mlayoutright.setVisibility(View.VISIBLE);
		}else {
			mlayoutleft.setVisibility(View.INVISIBLE);
			mlayoutright.setVisibility(View.INVISIBLE);
		}
	}
	
    public void  addview(Boolean isleftret){
        if(startview){
           return;
        }
        if(isleftret){
            layoutnavigationbar.windowAnimations = R.style.sideslip_bar_animations_left;
            layoutnavigationbar.gravity = Gravity.LEFT;
            mBarPanel.setBackgroundResource(R.drawable.nar_back_left);
            return_bt.setBackgroundResource(R.drawable.navigation_back_selector);
        }else{
            layoutnavigationbar.windowAnimations = R.style.sideslip_bar_animations_right;
            layoutnavigationbar.gravity = Gravity.RIGHT;
            mBarPanel.setBackgroundResource(R.drawable.nar_back_right);
            return_bt.setBackgroundResource(R.drawable.navigation_back_selector_right);
        }

        if(baseWindowManager != null){
			TvCommonManager.getInstance().setUsbAndroidTouchOnOff(false);
            baseWindowManager.addView(mBarPanel, layoutnavigationbar);
           // setinitdata();
            cancelTimeOutMessage();
            sendTimeOutMessage();
            startview = true;
        }
	
    }

    public void removeview(){
        if(startview){
			TvCommonManager.getInstance().setUsbAndroidTouchOnOff(true);
            cancelTimeOutMessage();
            baseWindowManager.removeView(mBarPanel);
            mlayoutleft.setVisibility(View.VISIBLE);
            mlayoutright.setVisibility(View.VISIBLE);
            startview = false;
        }

    }

    public void performKeyEvent(final int keyevent) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyevent);
                } catch (Exception e) {
                    Log.e("Exception when onBack", e.toString());
                }
            }
        } .start();
    }


    private void sendTimeOutMessage() {
        handler.sendEmptyMessageDelayed(SIDESLIP_BAR_TIMEOUT, SIDESLIP_BAR_OUTTIME_CANCEL);
    }

    private void cancelTimeOutMessage() {
        handler.removeMessages(SIDESLIP_BAR_TIMEOUT);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.nav_bar_left:
               isrileft = true;
                addview(true);
                if((mlayoutleft.getVisibility() == View.VISIBLE)&&(startview = true)){
                    mlayoutleft.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.nav_bar_right:
                isrileft = false;
                addview(false);
                if((mlayoutright.getVisibility() == View.VISIBLE)&&(startview = true)){
                    mlayoutright.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.navigation_back:
                performKeyEvent(KeyEvent.KEYCODE_BACK);
                break;
            case R.id.navigation_home:
                Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
                mHomeIntent.addCategory(Intent.CATEGORY_HOME);
                mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(mHomeIntent);
                removeview();
                break;
            case R.id.navigation_recent:
                Intent intent = new Intent();
                intent.setClassName("com.android.systemui", "com.android.systemui.recents.RecentsActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case R.id.navigation_folder:
                startAPP("com.hongling.hlfilemanager");
                removeview();
                break;
            case R.id.navigation_brush:
            	//enter the annotation of white board
            	removeview();
            	closeNavigationAndSlideNavigation();
            	handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						shotScreen();
					}
				}, 400);
                break;
            case R.id.navigation_source:
//                intent2.setClassName("com.hl.inputsource", "com.hl.inputsource.SourceSelectActivity");
                performKeyEvent(KeyEvent.KEYCODE_TV_INPUT);
                removeview();
                break;
            case R.id.navigation_setting:
//                intentSetting.setClassName("com.hongling.menusetting", "com.hongling.menusetting.MainActivity");
                startAPP("com.hongling.menusetting");
                removeview();
                break;

                default:
                    break;

        }
    }
    
    public boolean onTouch(View v, MotionEvent event) {
      
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()){
                    case R.id.nav_bar_left:
                    case R.id.nav_bar_right:
                    	try {
                    		TvCommonManager.getInstance().setUsbAndroidTouchOnOff(false);
						} catch (NoSuchMethodError e) {
							e.printStackTrace();
						}
                        isNavOnDown = true;
                        mTouchStartY = event.getY();
						lastY = event.getRawY();
                        break;
                    default:
                        break;
                }
                cancelTimeOutMessage();

                break;

            case MotionEvent.ACTION_MOVE:
                if(isNavOnDown) {
					if((event.getRawY() - lastY) > 20 || (event.getRawY() - lastY) < -20) {
						layoutnavigationbarleft.y = (int) (event.getRawY() - mTouchStartY - windowHeight / 2);
						layoutnavigationbarright.y = (int) (event.getRawY() - mTouchStartY - windowHeight / 2);
						layoutnavigationbar.y = (int) (event.getRawY() - mTouchStartY - windowHeight / 2);
						baseWindowManager.updateViewLayout(mlayoutleft, layoutnavigationbarleft);
						baseWindowManager.updateViewLayout(mlayoutright, layoutnavigationbarright);
					}
				}
                break;


            case MotionEvent.ACTION_UP:
				if(!startview){
					TvCommonManager.getInstance().setUsbAndroidTouchOnOff(true);
				}
                isNavOnDown = false;
                sendTimeOutMessage();
                break;

            case MotionEvent.ACTION_OUTSIDE:
                removeview();
                break;

        }
        return false;
    }
    public void startAPP(String appPackageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(
                    appPackageName);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context,context.getResources().getString(R.string.noAPP), Toast.LENGTH_LONG).show();
        }
    }
	private class NavigationStateChangeRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ON_NAVIGATION_CHANGED_ACTION.equals(action)) {
				isOnNavigation = intent.getExtras().getBoolean("NAVIGATION_STATE");
				Settings.Global.putInt(context.getContentResolver(), Settings.Global.NAVIGATION_ON, isOnNavigation ? 1 : 0);
				updateNavigationState();
			}
		}
	}
	
	public void unRegisterReceiver() {
		if(mReceiverRegiste) {
			context.unregisterReceiver(mNavigationStateChangeRecevier);
			mReceiverRegiste = false;
		}
	}
	
    private void shotScreen() {
        Bitmap screenshot = SurfaceControl.screenshot(1920, 1080);
//        Bitmap bitmap = BitmapCompressUtil.createScaledBitmap(screenshot,1920,1080);
//        Bitmap bitmap = BitmapCompressUtil.martix(screenshot,0.8f);
        String filePath = "";
//        String fileName = "";
        String hdFileName = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + "/HLBlackBoard/Notation/bg/";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String format = simpleDateFormat.format(new Date());
        hdFileName = format + "_hd.png";
        saveHdPicture(filePath, hdFileName, screenshot);

        if(!TextUtils.isEmpty(filePath + hdFileName)) {
            final Intent notationIntent  = new Intent();
            notationIntent.putExtra("screenShotPath_hd", filePath + hdFileName);
            ComponentName component = new ComponentName("com.example.hlwhiteboard7", "com.example.hlwhiteboard7.activity.SplashActivity");
            notationIntent.setComponent(component);
            notationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(notationIntent);
        }
    }
    
    private void saveHdPicture(final String filePath, final String fileName, final Bitmap screenshot) {
      File imgFile = new File(filePath, fileName);
      File parentFile = imgFile.getParentFile();
      if (!parentFile.exists()) {
          parentFile.mkdirs();
      }
      if (imgFile.exists()) {
          imgFile.delete();
      }
      OutputStream fout = null;
      try {
          imgFile.createNewFile();

          fout = new FileOutputStream(imgFile);
          screenshot.compress(Bitmap.CompressFormat.PNG, 80, fout);
          fout.flush();
      } catch (IOException e) {
          e.printStackTrace();
      } finally {
          try {
              if(fout != null) {
                  fout.close();
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
          if(screenshot != null) {
              screenshot.recycle();
          }

      }
  }


    private String saveBitmap(String filePath, String fileName, Bitmap screenshot) {
        File imgFile = new File(filePath, fileName);
        File parentFile = imgFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (imgFile.exists()) {
            imgFile.delete();
        }
        OutputStream fout = null;
        try {
            imgFile.createNewFile();

            fout = new FileOutputStream(imgFile);
            screenshot.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            fout.flush();
            return imgFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(fout != null) {
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(screenshot != null) {
                screenshot.recycle();
            }
        }
    }
    
    private void closeNavigationAndSlideNavigation() {
        // 发送此广播控制导航栏的开关
        Intent intentNavigation = new Intent(ON_NAVIGATION_CHANGED_ACTION);
        intentNavigation.putExtra("NAVIGATION_STATE", false);
        context.sendBroadcast(intentNavigation);
        // 关闭侧拉栏
        Intent intentSlideNavigation = new Intent(ON_SLIDE_NAVIGATION_CHANGED_ACTION);
        intentSlideNavigation.putExtra("SLIDE_NAVIGATION_STATE", false);
        context.sendBroadcast(intentSlideNavigation);
    }
}

