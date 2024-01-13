package net.estemon.studio.openkorridor.project

import CORRIDOR_CELL
import PAWN_CELL
import io.kvision.Application
import io.kvision.CoreModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapCssModule
import io.kvision.core.*
import io.kvision.html.Label
import io.kvision.html.Span
import io.kvision.module
import io.kvision.panel.FlexPanel
import io.kvision.panel.SimplePanel
import io.kvision.panel.gridPanel
import io.kvision.panel.root
import io.kvision.startApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

val AppScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

class App : Application() {

    override fun start(state: Map<String, Any>) {
        val root = root("kvapp") {
            gridPanel(
                    justifyContent = JustifyContent.CENTER,
                    alignItems = AlignItems.CENTER
            ) {
                add(Label("v0.0.1 [run 39]"))
                add(GameBoard())
            }
        }
        AppScope.launch {
            val pingResult = Model.ping("Hello world from client!")
            root.add(Span(pingResult))
        }
    }
}

class GameBoard : FlexPanel() {
    private var selectedCell: SimplePanel? = null

    init {
        gridPanel(
                templateRows = "repeat(9, 50px 10px)",
                templateColumns = "repeat(9, 50px 10px)",
                justifyContent = JustifyContent.CENTER,
                alignItems = AlignItems.CENTER
        ) {
            for (row in 1..17) {
                for (col in 1..18) {
                    val corridorCell = isCorridorCell(row, col)
                    val pawnCell = isPawnCell(row, col)
                    val boardCell = createBoardCell(row, col, corridorCell, pawnCell)
                    add(boardCell)

                    boardCell.enableTooltip(TooltipOptions(
                            title = "cell $row, $col",
                            placement = Placement.TOP,
                            triggers = listOf(Trigger.HOVER)
                    ))

                    boardCell.onClick {
                        if (corridorCell) {
                            boardCell.background = getDefaultBackground(pawnCell)
                        }
                    }
                }
            }
        }
    }

    private fun isVerticalCorridor(row: Int, col: Int): Boolean {
        return col % 2 == 0 && row % 2 == 1
    }

    private fun isHorizontalCorridor(row: Int, col: Int): Boolean {
        return col % 2 == 1 && row % 2 == 0
    }

    private fun isCorridorCell(row: Int, col: Int): Boolean {
        return isVerticalCorridor(row, col) || isHorizontalCorridor(row, col)
    }

    private fun isPawnCell(row: Int, col: Int): Boolean {
        return (row % 2 == 1 && col % 2 == 1)
    }

    private fun getDefaultBackground(pawnCell: Boolean): Background {
        return if (pawnCell) {
            Background(Color.name(Col.DARKGRAY))
        } else {
            Background(Color.name(Col.LIGHTGRAY))
        }
    }

    private fun createBoardCell(row: Int, col: Int, corridorCell: Boolean, pawnCell: Boolean): SimplePanel {

        return SimplePanel().apply {
            cursor = if (corridorCell || pawnCell) Cursor.POINTER else Cursor.DEFAULT
            // background = Background(Color.name(Col.LIGHTGRAY))
            width = CssSize(10, UNIT.px)
            height = CssSize(10, UNIT.px)

            if (col == 18) {
                width = CssSize(0, UNIT.px)
                height = CssSize(0, UNIT.px)
            } else if (pawnCell) {
                width = CssSize(50, UNIT.px)
                height = CssSize(50, UNIT.px)
                addCssClass(PAWN_CELL)
            } else if (isVerticalCorridor(row, col)) {
                height = CssSize(50, UNIT.px)
                addCssClass(CORRIDOR_CELL)
            } else if (isHorizontalCorridor(row, col)) {
                width = CssSize(50, UNIT.px)
                addCssClass(CORRIDOR_CELL)
            }
        }
    }
}

fun main() {
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        CoreModule
    )
}
