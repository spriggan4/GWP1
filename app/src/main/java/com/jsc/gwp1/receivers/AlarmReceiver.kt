package com.jsc.gwp1.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jsc.gwp1.R
import com.jsc.gwp1.SetActivityDaySchedule
import com.jsc.gwp1.data.CHANNEL_ID

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val textTitle = intent?.getStringExtra("textTitle")
        val textContent = intent?.getStringExtra("textContent")
        val ID = intent?.getIntExtra("ID", 0)


        // 앱에서의 활동에 대한 명시적 의도
        val notificationIntent = Intent(context, SetActivityDaySchedule::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        //알림을 클릭했을때 실행하는 액티비티 지정
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, notificationIntent, 0)


        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_alarm_24)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // 알림을 눌렀을때 실행시키고 알림은 삭제시키기 설정
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        //알림 보이기
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(ID!!, builder.build())
        }
    }
}