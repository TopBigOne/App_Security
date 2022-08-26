package com.jar.inlinehook.sandhooktwo;

import android.app.Application;

import com.swift.sandhook.SandHook;
import com.swift.sandhook.wrapper.HookErrorException;

/**
 * @author : dev
 * @version :
 * @Date :  2022/8/25 18:53
 * @Desc :
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SandHook.addHookClass(MainActivityHooker.class);
        } catch (HookErrorException e) {
            e.printStackTrace();
        }


    }
}
