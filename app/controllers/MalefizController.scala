package controllers

import javax.inject._

import play.api.mvc._

import de.htwg.se.malefiz.Malefiz
import de.htwg.se.malefiz.controller.State

@Singleton
class MalefizController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  //parse TUI String
  def boardString: String = Malefiz.controller.gameBoard.toString.replaceAll("( ?[0-9]+ )|(16)", "").replace(" ", "#").replace("###", " ").dropRight(1)

  //parse player Color
  def activePlayerColorString : String = {
    val activePlayerColorInt = Malefiz.controller.activePlayer.color

    var activePlayerColorString = ""


    if (activePlayerColorInt == 1) {
      activePlayerColorString = "Red"
    } else if (activePlayerColorInt == 2) {
      activePlayerColorString = "Green"
    } else if (activePlayerColorInt == 3) {
      activePlayerColorString = "Yellow"
    } else {
      activePlayerColorString = "Blue"
    }

    activePlayerColorString
  }

  //get diced
  def diced : String = Malefiz.controller.diced.toString


  //parse game state message
  def message : String = {
    Malefiz.controller.state match {
      case State.SetBlockStone =>
        "Set a BlockStone"

      case State.ChoosePlayerStone =>
        "Chose one of your Stones"

      case State.ChooseTarget =>
        "Chose a Target Field"

      case State.BeforeEndOfTurn =>
        "Press Enter to end your turn or Backspace to undo"

      case _ => "next turn"
    }
  }

  def malefiz = Action {
    Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message))
  }

  def info = Action {
    Ok(views.html.info())
  }

  def newGame(n: Int) = Action {
      Malefiz.controller.newGame(n)
      Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message))
  }

  def takeInput(x: Int, y: Int) = Action {
    Malefiz.controller.takeInput(x,y)
    Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message))
  }

  def endTurn() = Action {
    Malefiz.controller.endTurn()
    Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message))
  }

  def undo() = Action {
    Malefiz.controller.undo()
    Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message))
  }

  def redo() = Action {
    Malefiz.controller.redo()
    Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message))
  }
}