package com.example.rumatiabutbattletanks.drawers

import android.view.View
import android.widget.FrameLayout
import com.example.rumatiabutbattletanks.CELL_SIZE
import com.example.rumatiabutbattletanks.binding
import com.example.rumatiabutbattletanks.enums.Direction
import com.example.rumatiabutbattletanks.models.Coordinate
import com.example.rumatiabutbattletanks.models.Element
import com.example.rumatiabutbattletanks.utils.checkViewCanMoveThroughBorder
import com.example.rumatiabutbattletanks.utils.getElementByCoordinates

class TankDrawer(val container: FrameLayout) {
    var currentDirection = Direction.UP

    fun move(myTank: View, direction: Direction, elementsOnContainer: List<Element>) {
        val layoutParams = myTank.layoutParams as FrameLayout.LayoutParams
        val currentCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
        currentDirection = direction
        myTank.rotation = direction.rotation
        when(direction) {
            Direction.UP -> {
                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
            }

            Direction.DOWN -> {
                (myTank.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }

            Direction.LEFT -> {
                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE
            }

            Direction.RIGHT -> {
                (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
        }

        val nextCoordinate = Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
        if (myTank.checkViewCanMoveThroughBorder(nextCoordinate) && checkTankCanMoveThroughMaterial(nextCoordinate, elementsOnContainer))
        {
            binding.container.removeView(myTank)
            binding.container.addView(myTank)
        } else {
            (myTank.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
            (myTank.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
        }
    }

    private fun checkTankCanMoveThroughMaterial(coordinate: Coordinate, elementsOnContainer: List<Element>): Boolean {
        getTankCoordinates(coordinate).forEach {
            val element = getElementByCoordinates(it, elementsOnContainer)
            if (element != null && !element.material.tankCanGoThrough) {
                return false
            }
        }
        return true
    }

    private fun getTankCoordinates(topLeftCoordinate: Coordinate): List<Coordinate> {
        val coordinateList = mutableListOf<Coordinate>()
        coordinateList.add(topLeftCoordinate)
        coordinateList.add(Coordinate(topLeftCoordinate.top + CELL_SIZE, topLeftCoordinate.left))
        coordinateList.add(Coordinate(topLeftCoordinate.top, topLeftCoordinate.left + CELL_SIZE))
        coordinateList.add(Coordinate(topLeftCoordinate.top + CELL_SIZE, topLeftCoordinate.left + CELL_SIZE))
        return  coordinateList
    }
}