package com.guogu.serial;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	Intent startService;
	SharedPreferences spf;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("LZP", "onCreate");
	/*	startService = new Intent(this, PackageReceiveService.class);
		startService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(startService);*/
	//	finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(mUsbReceiver);
		// stopService(startService);
	}
}
