package com.jsc.gwp1.initSetting

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jsc.gwp1.ChoiceActivityScheduleAndWorkOut
import com.jsc.gwp1.R
import com.jsc.gwp1.data.canSleepHour
import com.jsc.gwp1.data.canSleepMin
import com.jsc.gwp1.data.numOfEatPerDay
import kotlinx.android.synthetic.main.activity_set_can_sleep_time.*
import org.jetbrains.anko.startActivity

class SetActivityCanSleepTime : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_can_sleep_time)

        moveToChoiceScheduleAndWorkOutBtn.setOnClickListener {

            if (Build.VERSION.SDK_INT >= 23) {
                canSleepHour = alarmTimePicker.getHour()
                canSleepMin = alarmTimePicker.getMinute()
            } else {
                canSleepHour = alarmTimePicker.currentHour
                canSleepMin = alarmTimePicker.currentMinute
            }
            startActivity<ChoiceActivityScheduleAndWorkOut>()
        }
    }
}