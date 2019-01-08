package controllers

import javax.inject._
import play.api.mvc._
import de.htwg.se.malefiz.Malefiz
import de.htwg.se.malefiz.controller.{ ControllerInterface, State }
import de.htwg.se.malefiz.model.gameboard.{ Field, GameBoardInterface, PlayerStone }
import play.api.libs.json._

@Singleton
class MalefizController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  //parse TUI String
  def boardString: String = Malefiz.controller.gameBoard.toString.replaceAll("( ?[0-9]+ )|(16)", "").replace(" ", "#").replace("###", " ").dropRight(1)

  //parse player Color
  def activePlayerColorString: String = Malefiz.controller.activePlayer.color.toString()

  //get diced
  def diced: String = Malefiz.controller.diced.toString

  //parse game state message
  def message: String = {
    Malefiz.controller.state match {
      case State.SetBlockStone =>
        "Set a BlockStone"

      case State.ChoosePlayerStone =>
        "Chose one of your Stones"

      case State.ChooseTarget =>
        "Chose a Target Field"

      case State.BeforeEndOfTurn =>
        "Press N to end your turn or U to undo"

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
    Malefiz.controller.takeInput(x, y)
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

  def gameJson() = Action {
    Ok(gameToJson(Malefiz.controller))
  }

  def gameToJson(controller: ControllerInterface): JsObject = {
    Json.obj(
      "activePlayer" -> JsNumber(controller.activePlayer.color),
      "diced" -> JsString(diced),
      "message" -> JsString(message),
      "rows" -> Json.toJson(
        for {
          y <- 0 to 15
        } yield rowToJson(controller.gameBoard, y)))
  }

  def rowToJson(gameBoard: GameBoardInterface, y: Int): JsObject = {
    Json.obj(
      "rowNr" -> JsNumber(y),
      "fields" -> Json.toJson(
        for {
          x <- 0 to 16
        } yield fieldToJson(gameBoard, x, y)))
  }

  def fieldToJson(gameBoard: GameBoardInterface, x: Int, y: Int): JsObject = {
    if (!gameBoard.board(x)(y).isFreeSpace()) {
      val field = gameBoard.board(x)(y).asInstanceOf[Field]
      val sort = field.stone.sort
      if (sort == 'p') {
        Json.obj(
          "isFreeSpace" -> JsBoolean(false),
          "x" -> JsNumber(x),
          "y" -> JsNumber(y),
          "sort" -> JsString(field.stone.asInstanceOf[PlayerStone].playerColor.toString),
          "avariable" -> JsBoolean(field.avariable))
      } else {
        Json.obj(
          "isFreeSpace" -> JsBoolean(gameBoard.board(x)(y).isFreeSpace()),
          "x" -> JsNumber(x),
          "y" -> JsNumber(y),
          "sort" -> JsString(sort.toString),
          "avariable" -> JsBoolean(field.avariable))
      }
    } else {
      Json.obj(
        "isFreeSpace" -> JsBoolean(true),
        "x" -> JsNumber(x),
        "y" -> JsNumber(y),
        "sort" -> JsString("f"),
        "avariable" -> JsBoolean(false))
    }
  }
}