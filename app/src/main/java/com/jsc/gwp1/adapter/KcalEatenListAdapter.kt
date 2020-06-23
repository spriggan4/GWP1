package com.jsc.gwp1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jsc.gwp1.R
import com.jsc.gwp1.data.KcalEaten
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class KcalEatenListAdapter(realmResult: OrderedRealmCollection<KcalEaten>) :
    RealmBaseAdapter<KcalEaten>(realmResult) {

    class KcalEatenViewHolder(view: View) {
        val tvKcalEatenClassification: TextView = view.findViewById(R.id.tvkcalClassification)
        val tvKcalNum: TextView = view.findViewById(R.id.tvKcalNum)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh: KcalEatenViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_kcal_eaten, parent, false)

            vh = KcalEatenViewHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as KcalEatenViewHolder
        }

        adapterData?.let {
            val item = it[position]
            vh.tvKcalEatenClassification.text = item.classification
            vh.tvKcalNum.text = item.kcalDate.toString()
        }

        return view
    }

    override fun getItemId(position: Int): Long {
        adapterData?.let {
            return it[position].id
        }
        return super.getItemId(position)
    }
}