/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tk.leoforney.doorreader;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyFMS extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    List<String> changeList;

    SharedPreferences pref;
    final static String PREF_KEY = "ChangeListPref";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        pref = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (!isForeground("tk.leoforney.doorreader")) {

                String changeListGson = pref.getString("changeList", null);

                if (changeListGson == null) {
                    changeList = new ArrayList<>();
                } else {
                    changeList = new Gson().fromJson(changeListGson, List.class);
                }

                changeList.add(remoteMessage.getData().get("change"));

                pref.edit().putString("changeList", new Gson().toJson(changeList)).apply();

                sendNotification();
                Log.d(TAG, Arrays.toString(changeList.toArray()));


            } else {
                Log.d(TAG, "deleted!");
                pref.edit().remove("changeList");
            }

        }

    }

    private void sendNotification() {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        for (int i = 0; i < changeList.size(); i++) {
            String iteratedString = changeList.get(i);
            style.addLine(iteratedString);
        }
        style.setBigContentTitle("Door changes");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Door changes")
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setAutoCancel(true);

        mBuilder.setStyle(style);

        Intent resultIntent = new Intent(this, HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(HomeActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());

    }

    public boolean isForeground(String myPackage) {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = am.getRunningTasks(1);

        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        if (componentInfo.getPackageName().equals(myPackage)) return true;
        return false;
    }
}