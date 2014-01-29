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

  def ws() = WebSocket.using[String] { request =>
    val (out, channel) = Concurrent.broadcast[String]

    val system = Akka.system()

    val boardActor = system.actorSelection("user/board")

    val userActor = system.actorOf(User.props(channel, boardActor))

    channel.push(message(new Message("me", "test")).body)

    val in = Iteratee.foreach[String] {
      msg =>
        userActor ! msg
    }.map { _ =>
        userActor ! User.ClientConnectionLost
    }

    (in, out)
  }

}