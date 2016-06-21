package com.ksddo.task2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by k_tetsuo on 2015/08/11.
 */

public class BootReceiver extends BroadcastReceiver
{
      @Override
      public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                  // サービスの起動
                  MainWearActivity.startLayerService(context);
                }
          }
}