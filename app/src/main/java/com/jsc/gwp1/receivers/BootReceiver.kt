//package com.jsc.gwp.receivers
//
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import java.util.*
//
//class SampleBootReceiver : BroadcastReceiver() {
//    private var alarmMgr: AlarmManager? = null
//    private lateinit var alarmIntent: PendingIntent
//
//    override fun onReceive(context: Context, intent: Intent) {
//        //재부팅시 설정
//        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
//            alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//            alarmIntent = Intent(context, AlarmReceiver::class.java).let { alarmIntent ->
//                PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
//            }
//
//            // 여기서 알림 시간 설정, 부팅 후니깐 시간 변경해야 함.
//            val calendar: Calendar = Calendar.getInstance().apply {
//                timeInMillis = System.currentTimeMillis()
//                set(Calendar.HOUR_OF_DAY, 0)
//                set(Calendar.MINUTE, 50)
//            }
//
//            // setInexactRepeating ()을 사용하면 AlarmManager 간격 상수 중 하나를 사용해야 함
//            alarmMgr?.setInexactRepeating(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
//                alarmIntent
//            )
//        }
//    }
//}