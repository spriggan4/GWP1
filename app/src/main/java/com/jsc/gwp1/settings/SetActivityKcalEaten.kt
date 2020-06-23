package com.jsc.gwp1.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jsc.gwp1.R
import com.jsc.gwp1.data.KcalEaten
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_set_kcal_eaten.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class SetActivityKcalEaten : AppCompatActivity() {
    private val TAG = "SetActivityKcalEaten"

    //내가 만드는건 예제와 틀리게 간단하다.
    //칼로리량만 입력하면 되고 대신 현재 시간이 입력되도록
    private val realm = Realm.getDefaultInstance()
    private val databaseNum = nextId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_kcal_eaten)

        runOnUiThread {
            tvKcalEatenTitle.text = "${databaseNum + 1} 번째 섭취"
        }

        // 업데이트 조건
        val id = intent.getLongExtra("id", -1)
        if (id == -1L) {
            insertMode()
        } else {
            updateMode(id)
        }
    }

    //초기화
    private fun insertMode() {
        //Log.d(TAG,"insertMode 들어옴")
        //삭제 버튼 감추기
        fabSubtract.hide()
        //Log.d(TAG,"fabSubtract 숨기기")

        fabAddDone.setOnClickListener {
            insertKcalEaten()
        }
    }

    private fun insertKcalEaten() {
        realm.beginTransaction()
        val kcalEaten = realm.createObject<KcalEaten>(databaseNum)
        kcalEaten.classification = "${databaseNum + 1} 번째 섭취"
        kcalEaten.kcalDate = etnKcalEaten.text.toString().toInt()
        realm.commitTransaction()

        alert("내용이 추가되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    private fun updateMode(id: Long) {
        realm.where<KcalEaten>().equalTo("id", id)
            .findFirst()?.apply {
                realm.beginTransaction()
                kcalDate = etnKcalEaten.text.toString().toInt()
                realm.commitTransaction()

                alert("내용이 추가되었습니다.") {
                    yesButton { finish() }
                }.show()
            }

        fabAddDone.setOnClickListener {
            updateKcalEaten(id)
        }

        fabSubtract.setOnClickListener {
            deleteKcalEaten(id)
        }
    }

    private fun updateKcalEaten(id: Long) {
        realm.where<KcalEaten>().equalTo("id", id)
            .findFirst()?.apply {
                realm.beginTransaction()
                kcalDate = etnKcalEaten.text.toString().toInt()
                realm.commitTransaction()

                alert("내용이 변경되었습니다.") {
                    yesButton { finish() }
                }.show()
            }
    }

    private fun deleteKcalEaten(id: Long) {
        realm.beginTransaction()
        val todo = realm.where<KcalEaten>().equalTo("id", id).findFirst()!!
        todo.deleteFromRealm()
        realm.commitTransaction()

        alert("내용이 삭제되었습니다.") {
            yesButton { finish() }
        }.show()
    }

    //realm 데이터베이스에 순차적으로 번호를 지정하기 위한 함수
    private fun nextId(): Int {
        val maxId = realm.where<KcalEaten>().max("id")
        if (maxId != null) {
            return maxId.toInt() + 1
        }
        return 0
    }
}