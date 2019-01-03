package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import de.htwg.se.malefiz.Malefiz
import de.htwg.se.malefiz.controller.ControllerInterface
import de.htwg.se.malefiz.controller.State.{BeforeEndOfTurn, ChoosePlayerStone, ChooseTarget, SetBlockStone}
import de.htwg.se.malefiz.model.gameboard.{Field, GameBoardInterface, PlayerStone}
import javax.inject._
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.swing.Reactor

@Singleton
class MalefizController @Inject()(cc: ControllerComponents) (implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {

  //parse TUI String
  def boardString: String = Malefiz.controller.gameBoard.toString.replaceAll("( ?[0-9]+ )|(16)", "").replace(" ", "#").replace("###", " ").dropRight(1)

  //parse player Color
  def activePlayerColorString: String = Malefiz.controller.activePlayer.color.toString()

  //get diced
  def diced: String = Malefiz.controller.diced.toString


  //parse game state message
  def message: String = {
    Malefiz.controller.state match {
      case SetBlockStone =>
        "Set a BlockStone"

      case ChoosePlayerStone =>
        "Chose one of your Stones"

      case ChooseTarget =>
        "Chose a Target Field"

      case BeforeEndOfTurn =>
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
      "fields" -> Json.toJson(
        for {
          x <- 0 to 16
          y <- 0 to 15
        } yield fieldToJson(controller.gameBoard, x, y)))
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
        "isFreeSpace" -> JsBoolean(true))
    }
  }


  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      println("Connect received")
      MalefizWebSocketActorFactory.create(out)
    }
  }

  object MalefizWebSocketActorFactory {
    def create(out: ActorRef) = {
      Props(new MalefizWebSocketActor(out))
    }
  }

  class MalefizWebSocketActor(out: ActorRef) extends Actor with Reactor{
    listenTo(Malefiz.controller)

    def receive = {
      case msg: String =>
        out ! gameToJson(Malefiz.controller).toString
        println("Sent Json to Client"+ msg)
    }

    reactions += {
      case event => sendJsonToClient
    }

    def sendJsonToClient = {
      println("Received event from Controller")
      out ! gameToJson(Malefiz.controller).toString
    }
  }


}