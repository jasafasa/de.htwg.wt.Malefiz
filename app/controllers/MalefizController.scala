package controllers

import javax.inject._

import play.api.mvc._

import de.htwg.se.malefiz.Malefiz
import de.htwg.se.malefiz.controller.State

@Singleton
class MalefizController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  //parse TUI String
  val malefizAsText = Malefiz.controller.gameBoard.toString.replaceAll("( ?[0-9]+ )|(16)", "").replace(" ", "#").replace("###", " ").dropRight(1)


  //parse player Color
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


  //get diced
  val diced = Malefiz.controller.diced.toString


  //parse game state message
  var message = "n.a."

  Malefiz.controller.state match {
    case State.SetBlockStone =>
      message = "Set a BlockStone"

    case State.ChoosePlayerStone =>
      message = "Chose one of your Stones"

    case State.ChooseTarget =>
      message = "Chose a Target Field"

    case State.BeforeEndOfTurn =>
      message = "Press Enter to end your turn or Backspace to undo"

    case _ => message = "next turn"
  }

  def malefiz = Action {
    Ok(views.html.malefiz(malefizAsText, activePlayerColorString, diced, message))
  }

  def info = Action {
    Ok(views.html.info())
  }
}