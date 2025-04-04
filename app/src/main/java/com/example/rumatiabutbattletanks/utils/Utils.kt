package com.example.rumatiabutbattletanks.utils

import android.view.View
import com.example.rumatiabutbattletanks.binding
import com.example.rumatiabutbattletanks.models.Coordinate
import com.example.rumatiabutbattletanks.models.Element

fun View.checkViewCanMoveThroughBorder(coordinate: Coordinate): Boolean {
    return coordinate.top >= 0 &&
            coordinate.top + this.height <= binding.container.height &&
            coordinate.left >= 0 &&
            coordinate.left + this.width <= binding.container.width
}

fun getElementByCoordinates(coordinate: Coordinate, elementsOnContainer: List<Element>) =
    elementsOnContainer.firstOrNull { it.coordinate == coordinate }