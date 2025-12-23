import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.Timer
import kotlin.random.Random

data class Point(val x: Int, val y: Int)
enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
class SnakeGame : JPanel() {
    private val cellSize = 25
    private val gridSize = 20
    private val timerDelay = 150
    private var direction = Direction.RIGHT
    private var snake = mutableListOf(Point(10, 10))
    private var food = randomFood()
    private var gameOver = false
    private val timer = Timer(timerDelay) { updateGame() }
    init {
        preferredSize = Dimension(
            gridSize * cellSize,
            gridSize * cellSize
        )
        background = Color.BLACK
        isFocusable = true
        requestFocusInWindow()
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                direction = when (e.keyCode) {
                    KeyEvent.VK_W, KeyEvent.VK_UP ->
                        if (direction != Direction.DOWN) Direction.UP else direction
                    KeyEvent.VK_S, KeyEvent.VK_DOWN ->
                        if (direction != Direction.UP) Direction.DOWN else direction
                    KeyEvent.VK_A, KeyEvent.VK_LEFT ->
                        if (direction != Direction.RIGHT) Direction.LEFT else direction
                    KeyEvent.VK_D, KeyEvent.VK_RIGHT ->
                        if (direction != Direction.LEFT) Direction.RIGHT else direction
                    else -> direction
                }
            }
        })
        timer.start()
    }
    private fun updateGame() {
        if (gameOver) return
        val head = snake.first()
        val newHead = when (direction) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }
        // столкновение со стеной
        if (newHead.x !in 0 until gridSize ||
            newHead.y !in 0 until gridSize
        ) {
            endGame()
            return
        }
        // столкновение с собой
        if (snake.contains(newHead)) {
            endGame()
            return
        }
        snake.add(0, newHead)
        // еда
        if (newHead == food) {
            food = randomFood()
        } else {
            snake.removeLast()
        }
        repaint()
    }
    private fun randomFood(): Point {
        while (true) {
            val point = Point(
                Random.nextInt(gridSize),
                Random.nextInt(gridSize)
            )
            if (!snake.contains(point)) return point
        }
    }
    private fun endGame() {
        gameOver = true
        timer.stop()
        repaint()
        JOptionPane.showMessageDialog(
            this,
            "Game Over!\nScore: ${snake.size - 1}"
        )
        restart()
    }
    private fun restart() {
        snake = mutableListOf(Point(10, 10))
        direction = Direction.RIGHT
        food = randomFood()
        gameOver = false
        timer.start()
    }
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g as Graphics2D
        // еда
        g2.color = Color.RED
        g2.fillRect(
            food.x * cellSize,
            food.y * cellSize,
            cellSize,
            cellSize
        )
        // змейка
        g2.color = Color.GREEN
        for (p in snake) {
            g2.fillRect(
                p.x * cellSize,
                p.y * cellSize,
                cellSize,
                cellSize
            )
        }
    }
}
fun main() {
    JFrame("Snake").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        add(SnakeGame())
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }
}
