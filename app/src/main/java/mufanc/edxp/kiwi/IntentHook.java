package mufanc.edxp.kiwi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class IntentHook implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private static final String targetPackageName = "com.kiwibrowser.browser";
    private static String modulePath;
    private Drawable background = null;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        modulePath = startupParam.modulePath;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(targetPackageName)) {
            return;
        }

        Log.i("KiwiLogcat", "Attaching: " + lpparam.processName);

        final Activity[] activity = new Activity[1];
        XposedHelpers.findAndHookMethod(
                "android.app.Activity",
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.i("KiwiLogcat", "Launching:" + param.thisObject.getClass().getName());
                        activity[0] = (Activity) param.thisObject;
//                        Exception exception = new Exception();
//                        String strace = Arrays.toString(exception.getStackTrace());
//                        Log.i("KiwiLogcat", "[*] Start Trace\n " + strace.replace(",", "\n")
//                                .replace("[", "")
//                                .replace("]", "") +
//                                "\n [*] End Trace"
//                        );
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                "android.app.Instrumentation",
                lpparam.classLoader,
                "callApplicationOnCreate",
                Application.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args[0] instanceof Application) {
                            loadResources((Application) param.args[0]);
                        }
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                "android.app.IActivityTaskManager$Stub$Proxy",
                lpparam.classLoader,
                "startActivity",
                XposedHelpers.findClass("android.app.IApplicationThread", lpparam.classLoader),
                String.class,
                String.class,
                Intent.class,
                String.class,
                android.os.IBinder.class,
                String.class,
                int.class,
                int.class,
                XposedHelpers.findClass("android.app.ProfilerInfo", lpparam.classLoader),
                android.os.Bundle.class,
                new XC_MethodHook() {
                    @Override
                    @SuppressLint("QueryPermissionsNeeded")
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = (Intent) param.args[3];
                        ComponentName componentName = intent.resolveActivity(activity[0].getPackageManager());
                        if (componentName != null && !componentName.getPackageName().equals(targetPackageName)) {
                            if (XposedHelpers.getAdditionalInstanceField(intent, "ACCEPT") == null) {
                                try {
                                    // 使用 PopupWindow
                                    PopupView popupView = new PopupView(activity[0], background, componentName.getPackageName(), intent);
                                    View root = activity[0].getWindow().getDecorView().getRootView();
                                    popupView.showAtLocation(root, Gravity.CENTER, 0, 0);
                                    // 使用「悬浮」控件
//                                FloatyView floatyView = new FloatyView(activity[0], background);
//                                View root = activity[0].getWindow().getDecorView().getRootView();
//                                ((FrameLayout) root).addView(floatyView);
                                } catch (Exception err) {
                                    Log.e("KiwiLogcat", "Error occurred while creating view:", err);
                                }
//                            activity[0].runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(
//                                            activity[0].getApplicationContext(),
//                                            "测试",
//                                            Toast.LENGTH_SHORT
//                                    ).show();
//                                }
//                            });
                                Log.i("KiwiLogcat", "Calling package: " + componentName.getPackageName());
                                Log.i("KiwiLogcat", "Intent: " + intent.toUri(0));
//                                String data = intent.getDataString();
//                                if (data != null) {
//                                    Log.i("KiwiLogcat", "Data: " + data);
//                                }
                                param.setResult(0);
                            } else {
                                Log.i("KiwiLogcat", "Accepted: " + intent.toString());
                            }
                        }
                    }
                }
        );
    }

    private void loadResources(Application application) {
        if (background != null) {
            return;
        }
        try {
            // 访问模块内置资源
            @SuppressLint("PrivateApi")
            Class<?> apkAssetsClass = Class.forName("android.content.res.ApkAssets");
            Method loadFromPath = apkAssetsClass.getMethod("loadFromPath", String.class);
            Object apkAssets = loadFromPath.invoke(null, modulePath);
            AssetManager assets = AssetManager.class.newInstance();
            Method setApkAssets = assets.getClass().getDeclaredMethod(
                    "setApkAssets",
                    Array.newInstance(apkAssetsClass, 0).getClass(),
                    boolean.class
            );
            Object assetsArray = Array.newInstance(apkAssetsClass, 1);
            Array.set(assetsArray, 0, apkAssets);
            setApkAssets.invoke(assets, assetsArray, false);

            // 获取资源对象
            Resources resources = new Resources(
                    assets,
                    application.getResources().getDisplayMetrics(),
                    application.getResources().getConfiguration()
            );
            PackageInfo packageInfo = application.getPackageManager().getPackageArchiveInfo(modulePath, PackageManager.GET_ACTIVITIES);
            Log.i("KiwiLogcat", "Loading resources from: " + packageInfo.packageName);
            int floatyViewID = resources.getIdentifier("floaty_window", "layout", packageInfo.packageName);
            int backgroundImageID = resources.getIdentifier("bilibili", "drawable", packageInfo.packageName);
            background = ResourcesCompat.getDrawable(resources, backgroundImageID, null);
        } catch (Exception err) {
            Log.e("KiwiLogcat", "Error occurred while loading resources:", err);
        }
    }
}
