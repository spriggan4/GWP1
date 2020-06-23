package com.jsc.gwp1.initSetting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jsc.gwp1.R
import com.jsc.gwp1.data.numOfEatPerDay
import kotlinx.android.synthetic.main.activity_set_can_eat_per_day.*
import org.jetbrains.anko.startActivity

class SetActivityCanEatPerDay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_can_eat_per_day)

        moveToSetCanSleepTimeBtn.setOnClickListener {
            numOfEatPerDay = inputMaximumNumberOfMealsPerDay.text.toString().toInt()
            startActivity<SetActivityCanSleepTime>()
        }
    }
}
