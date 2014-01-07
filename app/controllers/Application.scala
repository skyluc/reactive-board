package controllers

import play.api._
import play.api.mvc._
import model.Board
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.templates.Html
import model.Message

object Application extends Controller {

  def index = Action.async {
    Board.getLatestMessages.map { messages =>
      Ok(views.html.index(messages))
    }
  }
  
  def form(name: String, text: String) = Action {
    Board.addMessage(Message(name, text))
    Redirect("/")
  }

}