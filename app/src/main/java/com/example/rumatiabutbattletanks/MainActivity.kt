package com.example.rumatiabutbattletanks

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_SPACE
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.example.rumatiabutbattletanks.enums.Direction.UP
import com.example.rumatiabutbattletanks.enums.Direction.DOWN
import com.example.rumatiabutbattletanks.enums.Direction.LEFT
import com.example.rumatiabutbattletanks.enums.Direction.RIGHT
import com.example.rumatiabutbattletanks.databinding.ActivityMainBinding
import com.example.rumatiabutbattletanks.drawers.BulletDrawer
import com.example.rumatiabutbattletanks.drawers.GridDrawer
import com.example.rumatiabutbattletanks.drawers.ElementsDrawer
import com.example.rumatiabutbattletanks.drawers.EnemyDrawer
import com.example.rumatiabutbattletanks.enums.Direction
import com.example.rumatiabutbattletanks.enums.Material
import com.example.rumatiabutbattletanks.models.Coordinate
import com.example.rumatiabutbattletanks.models.Element
import com.example.rumatiabutbattletanks.models.Tank
import android.view.ViewTreeObserver.OnGlobalLayoutListener

const val CELL_SIZE = 50

lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var editMode = false

    private lateinit var playerTank: Tank
    private lateinit var eagle: Element

    private fun createTank(elementWidth: Int, elementHeight: Int): Tank {
        playerTank = Tank(
            Element(
                material = Material.PLAYER_TANK,
                coordinate = getPlayerTankCoordinate(elementWidth, elementHeight)
            ), UP,
            BulletDrawer(binding.container, elementsDrawer.elementsOnContainer, enemyDrawer)
        )
        return playerTank
    }

    private fun createEagle(elementWidth: Int, elementHeight: Int): Element {
        eagle = Element(
            material = Material.EAGLE,
            coordinate = getEagleCoordinate(elementWidth, elementHeight)
        )
        return eagle
    }

    private fun getPlayerTankCoordinate(width: Int, height: Int): Coordinate {
        val top = (height - height % 2) * CELL_SIZE -
                (height % 2) * CELL_SIZE -
                Material.PLAYER_TANK.height * CELL_SIZE
        val left = (width - width % 2) * CELL_SIZE / 2 -
                Material.EAGLE.width / 2 * CELL_SIZE -
                Material.PLAYER_TANK.width * CELL_SIZE
        return Coordinate(top, left)
    }

    private fun getEagleCoordinate(width: Int, height: Int): Coordinate {
        val top = (height - height % 2) * CELL_SIZE -
                (height % 2) * CELL_SIZE -
                Material.EAGLE.height * CELL_SIZE
        val left = (width - width % 2) * CELL_SIZE / 2 -
                Material.EAGLE.width / 2 * CELL_SIZE
        return Coordinate(top, left)
    }

    private val gridDrawer by lazy {
        GridDrawer(binding.container)
    }

    private val elementsDrawer by lazy {
        ElementsDrawer(binding.container)
    }

    private val levelStorage by lazy {
        LevelStorage( this )
    }

    private val enemyDrawer by lazy {
        EnemyDrawer(binding.container, elementsDrawer.elementsOnContainer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Menu"

        binding.editorClear.setOnClickListener { elementsDrawer.currentMaterial = Material.EMPTY }
        binding.editorBrick.setOnClickListener { elementsDrawer.currentMaterial = Material.BRICK }
        binding.editorConcrete.setOnClickListener {
            elementsDrawer.currentMaterial = Material.CONCRETE
        }
        binding.editorGrass.setOnClickListener { elementsDrawer.currentMaterial = Material.GRASS }
        binding.container.setOnTouchListener { _, event ->
            if (!editMode) {
                return@setOnTouchListener true
            }
            elementsDrawer.onTouchContainer(event.x, event.y)
            return@setOnTouchListener true
        }
        elementsDrawer.drawElementsList(LevelStorage.loadLevel())
        hideSettings()
        countWidthHeight()
    }

    private fun countWidthHeight() {
        val frameLayout = binding.container
        frameLayout.viewTreeObserver
            .addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    frameLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val elementWidth = frameLayout.width
                    val elementHeight = frameLayout.height

                    playerTank = createTank(elementWidth, elementHeight)
                    eagle = createEagle(elementWidth, elementHeight)

                    elementsDrawer.drawElementsList(listOf(playerTank.element, eagle))
                }
            })
    }

    private fun switchEditMode() {
        editMode = !editMode
        if (editMode) {
            showSettings()
        } else {
            hideSettings()
        }
    }

    private fun showSettings() {
        gridDrawer.drawGrid()
        binding.materialsContainer.visibility = VISIBLE
    }

    private fun hideSettings() {
        gridDrawer.removeGrid()
        binding.materialsContainer.visibility = INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                switchEditMode()
                return true
            }

            R.id.menu_save -> {
                levelStorage.saveLevel(elementsDrawer.elementsOnContainer)
                return true
            }

            R.id.menu_play -> {
                startTheGame()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startTheGame() {
        if (editMode) {
            return
        }
        enemyDrawer.startEnemyCreation()
        enemyDrawer.moveEnemyTanks()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KEYCODE_DPAD_UP -> move(UP)
            KEYCODE_DPAD_DOWN -> move(DOWN)
            KEYCODE_DPAD_LEFT -> move(LEFT)
            KEYCODE_DPAD_RIGHT -> move(RIGHT)
            KEYCODE_SPACE -> playerTank.bulletDrawer.makeBulletMove(playerTank)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun move(direction: Direction) {
        playerTank.move(direction, binding.container, elementsDrawer.elementsOnContainer)
    }
}