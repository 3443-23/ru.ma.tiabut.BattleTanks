package com.example.rumatiabutbattletanks.models

import android.view.View
import com.example.rumatiabutbattletanks.enums.Material

class Element constructor(
    val viewId:Int = View.generateViewId(),
    val material: Material,
    val coordinate: Coordinate,
    val width: Int,
    val height: Int
) {

}