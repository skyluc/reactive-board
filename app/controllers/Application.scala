package controllers

import play.api._
import play.api.mvc._
import model.Board
import model.Message
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {
  
  import Play.current
  
  def index(name: Option[String]) = Action.async {
    Board.getLatestMessages.map { messages =>
      Ok(views.html.index(messages, name.getOrElse("")))
    }
  }

  def form(name: String, text: String) = Action {
    Board.addMessage(Message(name, text))
    Redirect("/", Map("name" -> List(name)))
  }

}