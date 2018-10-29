package controllers

import javax.inject._

import play.api.mvc._

import de.htwg.se.malefiz.Malefiz

@Singleton
class MalefizController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def malefizAsText =  Malefiz.controller.gameBoard.toString.replaceAll("( ?[0-9]+ )|(16)","")

  def malefiz = Action {
    Ok(views.html.malefiz(malefizAsText))
  }

  def info = Action {
    Ok(views.html.info())
  }
}