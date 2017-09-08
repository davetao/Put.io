package com.stevenschoen.putionew;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.akaita.java.rxjava2debug.RxJava2Debug;
import com.crashlytics.android.Crashlytics;
import com.stevenschoen.putionew.model.files.PutioFile;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

public class PutioApplication extends MultiDexApplication {
	private PutioUtils utils;

	@Override
	public void onCreate() {
		super.onCreate();

		Fabric.with(this, new Crashlytics());

        JodaTimeAndroid.init(this);

		if (Build.VERSION.SDK_INT >= 26) {
			NotificationsKt.createNotificationChannels(this);
		}

        try {
            buildUtils();
        } catch (PutioUtils.NoTokenException e) {
            // User is not logged in
        }

        if (BuildConfig.DEBUG) {
	        RxJava2Debug.enableRxJava2AssemblyTracking();
        }
	}

    public static PutioApplication get(Context context) {
        return (PutioApplication) context.getApplicationContext();
    }

    public boolean isLoggedIn() {
        if (utils != null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            String token = sharedPrefs.getString("token", null);
            if (token != null && !token.isEmpty()) {
                return true;
            }
        } else {
            try {
                buildUtils();
                return isLoggedIn();
            } catch (PutioUtils.NoTokenException e) { }
        }

        return false;
    }

	public void buildUtils() throws PutioUtils.NoTokenException {
        utils = new PutioUtils(this);
    }

	public PutioUtils getPutioUtils() {
		return utils;
	}

    public interface CastCallbacks {
        void load(PutioFile file, String url, PutioUtils utils);
    }
}