package controllers

import javax.inject._

import play.api.mvc._
import de.htwg.se.sudoku.Sudoku
import de.htwg.se.sudoku.controller.controllerComponent.GameStatus

@Singleton
class SudokuController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val gameController = Sudoku.controller
  def tui =  gameController.gridToString + GameStatus.message(gameController.gameStatus)

  def sudoku = Action {
    Ok(views.html.sudoku(gameController))
  }

  def newGrid = Action {
    gameController.createNewGrid
    Ok(views.html.sudoku(gameController))
  }
}