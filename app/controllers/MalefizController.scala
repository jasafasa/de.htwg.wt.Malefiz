package controllers

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import de.htwg.se.malefiz.Malefiz
import de.htwg.se.malefiz.controller.{ ControllerInterface, State }
import de.htwg.se.malefiz.model.gameboard.{ Field, GameBoardInterface, PlayerStone }
import de.htwg.se.malefiz.util.Observer
import javax.inject._
import org.webjars.play.WebJarsUtil
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.Future
@Singleton
class MalefizController @Inject() (cc: ControllerComponents)(implicit webJarsUtil: WebJarsUtil, system: ActorSystem, assets: AssetsFinder, mat: Materializer, silhouette: Silhouette[DefaultEnv]) extends AbstractController(cc) with I18nSupport {

  //parse player Color
  def activePlayerColorString: String = Malefiz.controller.activePlayer.color.toString

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

  def malefiz: Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(views.html.malefiz(request.identity)))
  }

  def about: Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Future.successful(Ok(views.html.about(request.identity)))
  }

  def newGame(n: Int): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.newGame(n)
    Future.successful(Ok(views.html.malefiz(request.identity)))
  }

  def takeInput(x: Int, y: Int): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.takeInput(x, y)
    Future.successful(Ok(views.html.malefiz(request.identity)))
  }

  def endTurn(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.endTurn()
    Future.successful(Ok(views.html.malefiz(request.identity)))
  }

  def undo(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.undo()
    Future.successful(Ok(views.html.malefiz(request.identity)))
  }

  def redo(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
    Malefiz.controller.redo()
    Future.successful(Ok(views.html.malefiz(request.identity)))
  }

  def gameJson(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
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

  def socket: WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out =>
      println("Connect received")
      MalefizWebSocketActorFactory.create(out)
    }
  }

  object MalefizWebSocketActorFactory {
    def create(out: ActorRef): Props = {
      Props(new MalefizWebSocketActor(out))
    }
  }

  class MalefizWebSocketActor(out: ActorRef) extends Actor with Observer {

    Malefiz.controller.add(this)

    override def receive: Receive = {
      case _ => out ! gameToJson(Malefiz.controller).toString()
    }

    override def update(): Unit = {
      out ! gameToJson(Malefiz.controller).toString()
    }
  }
}