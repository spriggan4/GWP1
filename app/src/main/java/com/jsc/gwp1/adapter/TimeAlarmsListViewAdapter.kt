package com.jsc.gwp1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.jsc.gwp1.R
import com.jsc.gwp1.data.NeedSetTimeViewThings

class TimeAlarmsListViewAdapter: BaseAdapter() {
    private val TAG = "ListViewAdapter"

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private var listViewItemList = mutableListOf<NeedSetTimeViewThings>()

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val context = parent?.context

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (view == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.view_set_time, parent, false)
        }

        val classificationTV = view?.findViewById(R.id.tvClassification) as TextView
        val timeTV = view.findViewById(R.id.tvTime) as TextView

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        val listViewItem = listViewItemList[position]

        //분류 텍스트뷰 텍스트 설정
        classificationTV.setText(listViewItem.classification)

        if(listViewItem.min>10) {
            //시간 텍스트뷰 텍스트 설정
            timeTV.setText("${listViewItem.hour}:${listViewItem.min}")
        } else {
            timeTV.setText("${listViewItem.hour}:0${listViewItem.min}")
        }
        return view
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    override fun getItem(position: Int): NeedSetTimeViewThings {
        return listViewItemList[position]
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Adapter에 사용되는 데이터의 개수를 리턴
    override fun getCount(): Int {
        return listViewItemList.size
    }

    // 아이템 데이터 추가를 위한 함수.
    fun addItem(classification: String, hour: Int, min: Int) {
        val item = NeedSetTimeViewThings()

        item.classification = classification
        item.hour = hour
        item.min = min

        //리스트뷰 아이템 추가
        listViewItemList.add(item)
    }
}