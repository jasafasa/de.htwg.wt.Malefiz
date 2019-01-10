package controllers

import java.util.Observer

import akka.actor.{ Actor, ActorRef, ActorSystem, Identify, Props }
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import javax.inject._
import play.api.mvc._
import de.htwg.se.malefiz.Malefiz
import de.htwg.se.malefiz.controller.{ ControllerInterface, State }
import de.htwg.se.malefiz.model.gameboard.{ Field, GameBoardInterface, PlayerStone }
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.swing.Reactor
@Singleton
class MalefizController @Inject() (cc: ControllerComponents)(implicit webJarsUtil: WebJarsUtil, system: ActorSystem, assets: AssetsFinder, materializer: Materializer, silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) with I18nSupport {
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

  def malefiz = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message, request.identity)))
  }

  def about = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(views.html.about(request.identity)))
  }

  def newGame(n: Int) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.newGame(n)
    Future.successful(Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message, request.identity)))
  }

  def takeInput(x: Int, y: Int) = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.takeInput(x, y)
    Future.successful(Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message, request.identity)))
  }

  def endTurn() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.endTurn()
    Future.successful(Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message, request.identity)))
  }

  def undo() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.undo()
    Future.successful(Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message, request.identity)))
  }

  def redo() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.redo()
    Future.successful(Ok(views.html.malefiz(boardString, activePlayerColorString, diced, message, request.identity)))
  }

  def gameJson() = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(gameToJson(Malefiz.controller)))
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

  class MalefizWebSocketActor(out: ActorRef) extends Actor with Reactor {

    listenTo(Malefiz.controller)
    override def receive: Receive = {
      // at the moment msg is ignored and we sent always game board as json
      case _: String => {
        print("testingrec\n")
        sendClient()
      }
    }

    reactions += {
      case _ => {
        print("testingrea\n")
        sendClient()
      }
    }

    def sendClient(): Unit = {
      println("Received event from Controller")
      out ! gameToJson(Malefiz.controller).toString()
    }
  }
}