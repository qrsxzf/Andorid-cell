package com.qdb.agent.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 开机自启动
 * 
 * @author hj
 * 
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent screenService = new Intent(context, MyService.class);
		context.startService(screenService);
	}
}
