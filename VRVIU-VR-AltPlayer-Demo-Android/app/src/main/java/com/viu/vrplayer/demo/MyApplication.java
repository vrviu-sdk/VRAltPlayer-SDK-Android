package com.viu.vrplayer.demo;

import android.app.Activity;
import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @Title: com.silver.player.vrplayer
 * @Description: Added by kevin.zha@vrviu.com
 * @date 2017/10/21 17:19
 */

public class MyApplication extends android.app.Application
{
    public static boolean isPlaylistCached = false;
    private List<Activity> activities = new ArrayList<Activity>();
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
//        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
//        config.threadPriority(Thread.NORM_PRIORITY - 2);
//        config.denyCacheImageMultipleSizesInMemory();
//        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
//        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
//        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.writeDebugLogs(); // Remove for release app
//
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config.build());
    }


    /**应用实例**/
    private static MyApplication instance;
    /**
     *  获得实例
     * @return
     */
    public static MyApplication getInstance(){
        if (instance == null){
            instance = new MyApplication();
        }
        return instance;
    }
    /**
     * 新建了一个activity
     * @param activity
     */
    public void addActivity(Activity activity){
        if (activities == null){
            activities = new ArrayList<Activity>();
        }
        activities.add(activity);
    }
    /**
     *  结束指定的Activity
     * @param activity
     */
    public void finishActivity(Activity activity){
        if (activity!=null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }
    /**
     * 应用退出，结束所有的activity
     */
    public void exit(){
        for (Activity activity : activities) {
            if (activity!=null) {
                activity.finish();
            }
        }
        System.exit(0);
    }
    /**
     * 关闭Activity列表中的所有Activity*/
    public void finishActivity(){
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
