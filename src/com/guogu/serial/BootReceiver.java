package com.guogu.serial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	@Override 
    public void onReceive(Context context, Intent intent) { 
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){   
			    System.out.println("-->--BootBroadcastReceiver启动");
			    Intent startService = new Intent(context,PackageReceiveService.class);   
       			startService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
       			context.startService(startService);;		   
			  } 
	}

}
