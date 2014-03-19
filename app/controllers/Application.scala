package controllers

import play.api._
import play.api.mvc._
import model.Board
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.templates.Html
import model.Message
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Iteratee
import views.html.message
import play.libs.Akka
import model.User
import play.api.libs.json.JsValue
import play.api.libs.json.Json

object Application extends Controller {

  def index(name: Option[String]) = Action.async {
    Board.getLatestMessages.map { messages =>
      Ok(views.html.index(messages, name.getOrElse("")))
    }
  }

  def form(name: String, text: String) = Action {
    Board.addMessage(Message(name, text))
    Redirect("/", Map("name" -> List(name)))
  }

  def ws() = WebSocket.using[JsValue] { request =>
    val (out, channel) = Concurrent.broadcast[JsValue]

    val system = Akka.system()

    val boardActor = system.actorSelection(Play.current.configuration.getString("board.controller.path").get)

    val userActor = system.actorOf(User.props(channel, boardActor))

    val in = Iteratee.foreach[JsValue] {
      msg =>
        import model.Action
        import model.ActionJson._
        userActor ! Json.fromJson[Action](msg).get
    }.map { _ =>
        userActor ! User.ClientConnectionLost
    }

    (in, out)
  }

}