package com.example.rumatiabutbattletanks.models

import android.view.View
import android.widget.FrameLayout
import com.example.rumatiabutbattletanks.CELL_SIZE
import com.example.rumatiabutbattletanks.binding
import com.example.rumatiabutbattletanks.drawers.BulletDrawer
import com.example.rumatiabutbattletanks.enums.Direction
import com.example.rumatiabutbattletanks.enums.Material
import com.example.rumatiabutbattletanks.utils.checkIfChanceBiggerThanRandom
import com.example.rumatiabutbattletanks.utils.checkViewCanMoveThroughBorder
import com.example.rumatiabutbattletanks.utils.getElementByCoordinates
import com.example.rumatiabutbattletanks.utils.getTankByCoordinates
import com.example.rumatiabutbattletanks.utils.runOnUiThread
import kotlin.random.Random

class Tank(
    val element: Element,
    var direction: Direction,
    val bulletDrawer: BulletDrawer
) {
    fun move(direction: Direction, container: FrameLayout, elementsOnContainer: List<Element>) {
        val view = container.findViewById<View>(element.viewId) ?: return
        val currentCoordinate = getTankCurrentCoordinate(view)
        this.direction = direction
        view.rotation = direction.rotation
        val nextCoordinate = getTankNextCoordinate(view)
        if (view.checkViewCanMoveThroughBorder(nextCoordinate) && element.checkTankCanMoveThroughMaterial(nextCoordinate, elementsOnContainer)
        ) {
            emulateViewMoving(container, view)
            element.coordinate = nextCoordinate
            generateRandomDirectionForEnemyTank()
        } else {
            element.coordinate = currentCoordinate
            (view.layoutParams as FrameLayout.LayoutParams).topMargin = currentCoordinate.top
            (view.layoutParams as FrameLayout.LayoutParams).leftMargin = currentCoordinate.left
            changeDirectionForEnemyTank()
        }
    }

    private fun generateRandomDirectionForEnemyTank() {
        if (element.material != Material.ENEMY_TANK) {
            return
        }
        if (checkIfChanceBiggerThanRandom(10)) {
            changeDirectionForEnemyTank()
        }
    }

    private fun changeDirectionForEnemyTank() {
        if (element.material == Material.ENEMY_TANK) {
            val randomDirection = Direction.entries[Random.nextInt(Direction.entries.size)]
            this.direction = randomDirection
        }
    }


    private fun emulateViewMoving(container: FrameLayout, view: View) {
        container.runOnUiThread {
            binding.container.removeView(view)
            binding.container.addView(view, 0)
        }
    }


    private fun getTankCurrentCoordinate(tank: View): Coordinate {
        return Coordinate(
            (tank.layoutParams as FrameLayout.LayoutParams).topMargin,
            (tank.layoutParams as FrameLayout.LayoutParams).leftMargin
        )
    }

    private fun getTankNextCoordinate(view: View): Coordinate {
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams
        when(direction) {
            Direction.UP -> {
                (view.layoutParams as FrameLayout.LayoutParams).topMargin += -CELL_SIZE
            }

            Direction.DOWN -> {
                (view.layoutParams as FrameLayout.LayoutParams).topMargin += CELL_SIZE
            }

            Direction.LEFT -> {
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin -= CELL_SIZE
            }

            Direction.RIGHT -> {
                (view.layoutParams as FrameLayout.LayoutParams).leftMargin += CELL_SIZE
            }
        }
        return Coordinate(layoutParams.topMargin, layoutParams.leftMargin)
    }

    private fun Element.checkTankCanMoveThroughMaterial(
        coordinate: Coordinate,
        elementsOnContainer: List<Element>
    ): Boolean {
        for (anyCoordinate in getTankCoordinates(coordinate)) {
            var element = getElementByCoordinates(anyCoordinate, elementsOnContainer)
            if (element == null) {
                element = getTankByCoordinates(anyCoordinate, bulletDrawer.enemyDrawer.tanks)
            }
            if (element != null && !element.material.tankCanGoThrough) {
                if (this == element) {
                    continue
                }
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