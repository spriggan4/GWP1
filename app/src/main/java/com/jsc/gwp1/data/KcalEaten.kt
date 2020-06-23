package com.jsc.gwp1.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class KcalEaten(
    @PrimaryKey var id: Long = 0,
    var classification: String = "",
    var kcalDate: Int = 0
) : RealmObject()