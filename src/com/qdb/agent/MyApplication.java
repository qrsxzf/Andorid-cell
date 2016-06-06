package com.qdb.agent;

import org.simple.eventbus.EventBus;

import android.app.Application;
import android.content.Intent;

import com.qdb.agent.receiver.MyService;

public class MyApplication extends Application {

	private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		EventBus.getDefault().register(this);
		instance = this;
		Intent screenService = new Intent(this, MyService.class);
		startService(screenService);
	}

	public static MyApplication getInstance() {
		return instance;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		EventBus.getDefault().unregister(this);
	}

}
