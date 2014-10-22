package controllers

import play.api._
import play.api.mvc._
import model._
import scala.concurrent.ExecutionContext.Implicits.global
import model.JsonSupport._

object Application extends Controller {
  
  import Play.current
  
  def index(name: Option[String]) = Action {
        Ok(views.html.index(name.getOrElse("")))
  }

  def form(name: String, text: String) = Action {
    Board.addMessage(Message(name, text))
    Redirect("/", Map("name" -> List(name)))
  }
  
   /**
   * The WebSocket
   */
  def ws = WebSocket.acceptWithActor[ClientEvent, ClientEvent] { _ => upstream =>
    User.props(upstream, Actors.board)
  }

}