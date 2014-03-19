package model

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSelection
import views.html.message
import akka.actor.PoisonPill
import play.api.libs.json.JsValue
import play.api.libs.json.Json

class User(channel: Concurrent.Channel[JsValue], board: ActorSelection) extends Actor {

  def receive = {
    case Action("go", _, _, _) =>
      board ! Board.SubscribeAndGetAll
    case Action("msg", Some(user), Some(text), _) =>
      board ! Board.AddMessage(Message(user, text))
    case Action("edit", Some(name), _, Some(true)) =>
      board ! Board.StartEdit(name)
    case Action("edit", Some(name), _, Some(false)) =>
      board ! Board.StopEdit(name)
    case User.ClientConnectionLost =>
      board ! Board.Unsubscribe
      self ! PoisonPill
    case Board.Messages(messages) =>
      messages.foreach { m =>
        import model.ActionJson._
        channel.push(Json.toJson(Action("msg", Some(m.name), Some(m.text), None)))
      }
    case Board.Editors(editors) =>
      import model.ActionJson._
      channel.push(Json.toJson(Action("editors", None, Some(editors.mkString(", ")), None)))
  }

  override def postStop() {
    channel.eofAndEnd()
  }

}

object User {

  object ClientConnectionLost

  def props(channel: Concurrent.Channel[JsValue], board: ActorSelection) =
    Props(classOf[User], channel, board)

}