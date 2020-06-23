package com.jsc.gwp1

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.jsc.gwp1.adapter.TimeAlarmsListViewAdapter
import com.jsc.gwp1.data.*
import com.jsc.gwp1.receivers.AlarmReceiver
import com.jsc.gwp1.data.NeedSetTimeViewThings
import java.util.*


class SetActivityDaySchedule : AppCompatActivity() {
    val timesArrayList = mutableListOf<NeedSetTimeViewThings>()

    private val timeAlarmManagerRequestCodeList = mutableListOf<Int>()
    private var alarmMgr: AlarmManager? = null
    private var alarmIntentList = mutableListOf<PendingIntent?>()
    private val callbackMethodList = mutableListOf<OnTimeSetListener>()
    val adapter = TimeAlarmsListViewAdapter()

    private val calculatedNum = 16 / numOfEatPerDay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_day_schedule)

        //알람 매니져 선언
        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        loadData()

        //과거에 저장했던 데이터가 없으면 설정
        if (timesArrayList[0].hour == 100) {
            Log.d("TAG", "식사시간 설정 시작")
            //초기화 필요해 클리어.
            timesArrayList.clear()
        }
        //잠자는 시간을 기준으로 첫번째 식사 시간 계산
        timesArrayList.add(
            NeedSetTimeViewThings(
                "1번째 식사",
                canSleepHour + 8,
                canSleepMin
            )
        )
        if (timesArrayList[0].hour > 24) {
            timesArrayList[0].hour -= 24
        }

        //첫번째 식사를 기준으로 뒤에 먹는 식사 시간 계산
        for (i in 1 until numOfEatPerDay) {
            timesArrayList.add(
                NeedSetTimeViewThings(
                    "${i + 1}번째 식사",
                    timesArrayList[i - 1].hour + calculatedNum,
                    timesArrayList[i - 1].min
                )
            )
        }
        timesArrayList.add(
            NeedSetTimeViewThings(
                "수면시간",
                canSleepHour,
                canSleepMin
            )
        )


        val listView = findViewById<View>(R.id.lv_set_time) as ListView
        listView.adapter = adapter

        for (i in 0 until numOfEatPerDay) {
            //먹을 수 있는 횟수만큼 아답터에 정보 추가
            adapter.addItem(
                timesArrayList[i].classification,
                timesArrayList[i].hour,
                timesArrayList[i].min
            )
            //리퀘스트코드 리스트 개수만큼 선언
            timeAlarmManagerRequestCodeList.add(i + 101)
            //펜딩인텐트 리스트 개수만큼 선언
            alarmIntentList.add(Intent(this, AlarmReceiver::class.java).let { intent ->
                intent.putExtra("textTitle", "${i + 1}번째 식사시간")
                intent.putExtra("textContent", "밥 먹을 시간이에요")
                intent.putExtra("ID", (i + 0) * 111)
                PendingIntent.getBroadcast(
                    this,
                    timeAlarmManagerRequestCodeList[i],
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            })
            //리스트만큼 식사시간 알림 선언
            setNotificationAlarm(
                alarmMgr, alarmIntentList[i],
                adapter.getItem(i).hour, adapter.getItem(i).min
            )
            //리스트만큼 식사 콜백 메서드 선언
            callbackMethodList.add(
                i,
                //timePickerDialog에서 시간 설정 후 OK 누르면 실행되는 코드
                OnTimeSetListener { view, hourOfDay, minute ->
                    //설정한 시간들로 알림 설정
                    setNotificationAlarm(
                        alarmMgr, alarmIntentList[i],
                        hourOfDay, minute
                    )
                    //설정한 시간들 저장
                    adapter.getItem(i).hour = hourOfDay
                    adapter.getItem(i).min = minute
                    //리스트뷰 화면 갱신
                    adapter.notifyDataSetChanged()
                })
        }

        setSleepView(adapter)

        //리스트뷰의 클릭리스너 선언
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val item = parent.getItemAtPosition(position) as NeedSetTimeViewThings

                onClickHandler(
                    callbackMethodList[position],
                    item.hour,
                    item.min
                )
            }
    }

    override fun onStop() {
        super.onStop()
        saveData(adapter)
    }

    private fun onClickHandler(
        callbackMethod: OnTimeSetListener?,
        mHour: Int,
        mMin: Int
    ) {
        val dialog =
            TimePickerDialog(this, callbackMethod, mHour, mMin, true)
        dialog.show()
    }

    private fun setNotificationAlarm(
        alarmMgr: AlarmManager?,
        alarmIntent: PendingIntent?,
        mHour: Int,
        mMin: Int
    ) {
//    // 여기서 알림 시간 설정
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, mHour)
            set(Calendar.MINUTE, mMin)
        }

        // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmMgr?.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    private fun setSleepView(adapter: TimeAlarmsListViewAdapter) {
        //리스트뷰에 추가, turnNum은 의미 없음.
        adapter.addItem(
            timesArrayList[numOfEatPerDay].classification,
            timesArrayList[numOfEatPerDay].hour,
            timesArrayList[numOfEatPerDay].min
        )
        timeAlarmManagerRequestCodeList.add(numOfEatPerDay + 101)
        //펜딩인텐트 리스트 개수만큼 선언
        alarmIntentList.add(Intent(this, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("textTitle", "잠 잘 시간")
            intent.putExtra("textContent", "잠 잘 시간이에요")
            intent.putExtra("ID", numOfEatPerDay * 111)
            PendingIntent.getBroadcast(
                this,
                timeAlarmManagerRequestCodeList[numOfEatPerDay],
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        })
        //리스트만큼 식사시간 알림 선언
        setNotificationAlarm(
            alarmMgr, alarmIntentList[numOfEatPerDay],
            adapter.getItem(numOfEatPerDay).hour, adapter.getItem(numOfEatPerDay).min
        )
        //리스트만큼 식사 콜백 메서드 선언
        callbackMethodList.add(
            numOfEatPerDay,
            //timePickerDialog에서 시간 설정 후 OK 누르면 실행되는 코드
            OnTimeSetListener { view, hourOfDay, minute ->
                //설정한 시간들로 알림 설정
                setNotificationAlarm(
                    alarmMgr, alarmIntentList[numOfEatPerDay],
                    hourOfDay, minute
                )
                //설정한 시간들 저장
                adapter.getItem(numOfEatPerDay).hour = hourOfDay
                adapter.getItem(numOfEatPerDay).min = minute
                //리스트뷰 화면 갱신
                adapter.notifyDataSetChanged()
            })
    }

    private fun saveData(adapter: TimeAlarmsListViewAdapter) {
        val sharedPref =
            this.getSharedPreferences(
                getString(R.string.set_activity_day_schedule_file_key),
                Context.MODE_PRIVATE
            )
                ?: return
        with(sharedPref.edit()) {
            for (i in 0 until adapter.count - 1) {
                putInt("${i}번째hour", adapter.getItem(i).hour)
                putInt("${i}번째min", adapter.getItem(i).min)
            }
            putInt("수면시간hour", adapter.getItem(adapter.count - 1).hour)
            putInt("수면시간min", adapter.getItem(adapter.count - 1).min)
            commit()
        }
    }

    private fun loadData() {
        val sharedPref =
            this.getSharedPreferences(
                getString(R.string.set_activity_day_schedule_file_key),
                Context.MODE_PRIVATE
            )
                ?: return
        for (i in 0 until numOfEatPerDay) {
            timesArrayList.add(
                NeedSetTimeViewThings(
                    //classification은 리스트뷰에 출력되는 Text
                    "${i + 1}번째 식사",
                    sharedPref.getInt("${i}번째hour", 100),
                    sharedPref.getInt("${i}번째min", 100)
                )
            )
        }
        timesArrayList.add(
            NeedSetTimeViewThings(
                //classification은 리스트뷰에 출력되는 Text
                "수면시간",
                sharedPref.getInt("수면시간hour", 100),
                sharedPref.getInt("수면시간min", 100)
            )
        )
    }
}