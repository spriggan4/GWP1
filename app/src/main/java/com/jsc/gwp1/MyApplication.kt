package com.jsc.gwp1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.jsc.gwp1.data.CHANNEL_ID
import io.realm.Realm

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        // API 26+에서만 NotificationChannel을 작성.
        // NotificationChannel 클래스는 예전 지원 라이브러리에 없다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GWP"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            // 시스템에 등록하기
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}