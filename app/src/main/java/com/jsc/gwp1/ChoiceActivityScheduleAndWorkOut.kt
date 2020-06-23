package com.jsc.gwp1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.jsc.gwp1.adapter.KcalEatenListAdapter
import com.jsc.gwp1.data.*
import com.jsc.gwp1.initSetting.SetActivityGender
import com.jsc.gwp1.settings.SetActivityKcalEaten
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_set_day_schedule.*
import kotlinx.android.synthetic.main.activity_set_kcal_eaten.*
import kotlinx.android.synthetic.main.choice_schedule_app_bar.*
import kotlinx.android.synthetic.main.list_view_kcal_eaten.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.*
import java.util.*

class ChoiceActivityScheduleAndWorkOut : AppCompatActivity() {
    private val TAG = "ScheduleAndWorkOut"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toast: Toast
    private val realm = Realm.getDefaultInstance()
    private var backKeyPressedTime: Long = 0

    //private val adapter = KcalToEatExtensionDataAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_schedule_and_work_out)

        val sharedPref = this.getSharedPreferences(
            getString(R.string.setting_nums_file_key),
            Context.MODE_PRIVATE
        )

        GlobalScope.launch(Dispatchers.Default) {
            with(sharedPref.edit()) {
                putInt(getString(com.jsc.gwp1.R.string.set_gender_key), com.jsc.gwp1.data.gender)
                putInt(
                    getString(com.jsc.gwp1.R.string.set_birth_year_key),
                    com.jsc.gwp1.data.birthYear
                )
                putInt(
                    getString(com.jsc.gwp1.R.string.set_birth_month_key),
                    com.jsc.gwp1.data.birthMonth
                )
                putInt(
                    getString(com.jsc.gwp1.R.string.set_birth_day_key),
                    com.jsc.gwp1.data.birthDay
                )
                putInt(getString(com.jsc.gwp1.R.string.set_height_key), com.jsc.gwp1.data.height)
                putInt(
                    getString(com.jsc.gwp1.R.string.set_current_weight_key),
                    com.jsc.gwp1.data.curWeight
                )
                putInt(
                    getString(com.jsc.gwp1.R.string.set_desire_weight_key),
                    com.jsc.gwp1.data.desireWeight
                )
                putFloat(
                    getString(com.jsc.gwp1.R.string.set_action_time_in_week_key),
                    com.jsc.gwp1.data.actionTimeInAWeek
                )
                putInt(
                    getString(com.jsc.gwp1.R.string.set_can_eat_per_day_key),
                    com.jsc.gwp1.data.numOfEatPerDay
                )
                putInt(
                    getString(com.jsc.gwp1.R.string.set_can_sleep_time_hour_key),
                    com.jsc.gwp1.data.canSleepHour
                )
                putInt(
                    getString(com.jsc.gwp1.R.string.set_can_sleep_time_min_key),
                    com.jsc.gwp1.data.canSleepMin
                )
                commit()
            }

            GlobalScope.launch(Dispatchers.Default) {
                val rightNow = Calendar.getInstance()
                age = rightNow.get(Calendar.YEAR) - birthYear

                //생일 안지났으면 한살 내리기
                if (birthMonth * 100 + birthDay >
                    rightNow.get(Calendar.MONTH) * 100 + rightNow.get(Calendar.DAY_OF_MONTH)
                ) {
                    --age
                }

                //살 찌기 위해 필요한 칼로리 남녀 나눠서 계산하기, 1=남자 2=여자
                if (gender == 1) {
                    //남자
                    needCarl =
                        (66.47 + (13.75 * curWeight) + (5 * height) - (6.76 * age) + 500) * actionTimeInAWeek
                } else if (gender == 2) {
                    //여자
                    needCarl =
                        (655.1 + (9.56 * curWeight) + (1.85 * height) - (4.68 * age) + 500) * actionTimeInAWeek
                }
            }
        }

        //설정된 전체 칼로리를 가져와 설정 순서대로 표시
        val realmResult = realm.where<KcalEaten>()
            .findAll()
            .sort("id", Sort.ASCENDING)

        val adapter = KcalEatenListAdapter(realmResult)
        lvKcalEaten.adapter = adapter

        for (kcalEatenRealm in realmResult) {
            totalKcalNum =+ kcalEatenRealm.kcalDate
        }

        // 데이터가 변경되면 어댑터에 적용
        realmResult.addChangeListener { _ -> adapter.notifyDataSetChanged() }

        lvKcalEaten.setOnItemClickListener { parent, view, position, id ->
            // 먹은 칼로리 수정
            startActivity<SetActivityKcalEaten>("id" to id)
            Log.d(TAG, "lvKcalEaten 클릭 리스너 실행")
        }
        // 먹은 칼로리 추가
        fabAddKcalEaten.setOnClickListener {
            startActivity<SetActivityKcalEaten>()
            Log.d(TAG, "fabAddKcalEaten 클릭 리스너 실행")
        }

        val toolBar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolBar)

        //액션바 메뉴를 왼쪽으로 이동
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //액션바 아이콘 설정
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_view_headline_24)

        drawerLayout = findViewById(R.id.choice_schedule_drawer_layout) as DrawerLayout
        val navigationView = findViewById(R.id.choice_schedule_nav_view) as NavigationView

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            //서랍 열기 동작을 설명하는 문자열 리소스
            R.string.choice_schedule_drawer_open,
            //서랍 닫기 조치를 설명하는 문자열 리소스
            R.string.choice_schedule_drawer_close
        )
        //드로워 리스너 선언
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        //메뉴 선택시 실행되는 기능
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_reset_information -> alert("설정을 다시 하실껀가요?") {
                    yesButton {
                        gender = 100
                        birthYear = 100
                        birthMonth = 100
                        birthDay = 100
                        height = 100
                        curWeight = 100
                        desireWeight = 100
                        actionTimeInAWeek = 100F
                        numOfEatPerDay = 100
                        canSleepHour = 100
                        canSleepMin = 100

                        totalKcalNum = 0
                        startActivity<SetActivityGender>()
                    }
                    noButton {

                    }
                    onCancelled {

                    }
                }.show()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        scheduleBtn.setOnClickListener {
            startActivity<SetActivityDaySchedule>()
        }

        runOnUiThread {
            val averageKcal = Math.round(needCarl / numOfEatPerDay)
            KcalToEatDuringTheDayText.text = "오늘 섭취량 $totalKcalNum/${Math.round(needCarl)}Carl"
            averageKcalToEatText.text =
                "끼니당 평균 필요 섭취량은 $averageKcal"
        }
    }

    override fun onBackPressed() {
        //뒤로 가기 버튼 누를 경우 메뉴가 열려져 있으면 닫음.
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            //현재 시간을 통해서 만들기
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis()
                toast = Toast.makeText(
                    this,
                    "'뒤로' 버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT
                )
                toast.show()
                //토스트만 뛰우고 일단 함수 중료
                return
            }
            //2초안에 다시 누르면 앱종료
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                toast.cancel()
                this.finishAffinity()
            }
        }
    }

    private fun addingToTotalKcal() {

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}