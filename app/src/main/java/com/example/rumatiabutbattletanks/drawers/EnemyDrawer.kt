package com.example.rumatiabutbattletanks.drawers

import android.widget.FrameLayout
import com.example.rumatiabutbattletanks.CELL_SIZE
import com.example.rumatiabutbattletanks.binding
import com.example.rumatiabutbattletanks.enums.CELLS_TANKS_SIZE
import com.example.rumatiabutbattletanks.enums.Direction
import com.example.rumatiabutbattletanks.enums.Material
import com.example.rumatiabutbattletanks.models.Coordinate
import com.example.rumatiabutbattletanks.models.Element
import com.example.rumatiabutbattletanks.models.Tank
import com.example.rumatiabutbattletanks.utils.checkIfChanceBiggerThanRandom
import com.example.rumatiabutbattletanks.utils.drawElement

private const val MAX_ENEMY_AMOUNT = 20

class EnemyDrawer(
    private val container: FrameLayout,
    private val elements: MutableList<Element>
) {
    private val respawnList: List<Coordinate>
    private var enemyAmount = 0
    private var currentCoordinate:Coordinate
    val tanks = mutableListOf<Tank>()
    private var moveAllTanksThread: Thread? = null

    init {
        respawnList = getRespawnList()
        currentCoordinate = respawnList[0]
    }

    private fun getRespawnList(): List<Coordinate> {
        val respawnList = mutableListOf<Coordinate>()
        respawnList.add(Coordinate(top = 0, left = 0))
        respawnList.add(
            Coordinate(
                top = 0,
                left = ((container.width - container.width % CELL_SIZE) / CELL_SIZE -
                        (container.width - container.width % CELL_SIZE) / CELL_SIZE * 2) *
                        CELL_SIZE / 2 - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        respawnList.add(
            Coordinate(
                top = 0,
                left = (container.width - container.width % CELL_SIZE) - CELL_SIZE * CELLS_TANKS_SIZE
            )
        )
        return respawnList
    }

    private fun drawEnemy() {
        var index = respawnList.indexOf(currentCoordinate) + 1
        if (index == respawnList.size) {
            index = 0
        }
        currentCoordinate = respawnList[index]
        val enemyTank = Tank(
            Element(
                material = Material.ENEMY_TANK,
                coordinate = currentCoordinate
            ), Direction.DOWN,
            BulletDrawer(container, elements, this)
        )
        enemyTank.element.drawElement(container)
        tanks.add(enemyTank)
    }

    fun moveEnemyTanks() {
        Thread(Runnable {
            while (true) {
                goThroughAllTanks()
                Thread.sleep(400)
            }
        }).start()
    }

    private fun goThroughAllTanks() {
        moveAllTanksThread = Thread(Runnable {
            tanks.forEach {
                it.move(it.direction, container, elements)
                if (checkIfChanceBiggerThanRandom(10)) {
                    it.bulletDrawer.makeBulletMove(it)
                }
            }
        })
        moveAllTanksThread?.start()
    }

    fun startEnemyCreation() {
        Thread(Runnable {
            while (enemyAmount < MAX_ENEMY_AMOUNT) {
                drawEnemy()
                enemyAmount++
                Thread.sleep(3000)
            }
        }).start()
    }

    fun removeTank(tankIndex: Int) {
        if (tankIndex < 0) return
        moveAllTanksThread?.join()
        tanks.removeAt(tankIndex)
    }
}

