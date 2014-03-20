package model

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSelection
import akka.actor.PoisonPill
import play.api.libs.json.JsValue
import play.api.libs.json.Json

class User(channel: Concurrent.Channel[JsValue], board: ActorSelection) extends Actor {

  def receive = {
    case Action("go", _, _, _) =>
      board ! Board.SubscribeAndGetAll
    case Action("message", Some(name), Some(text), _) =>
      board ! Board.AddMessage(Message(name, text))
    case Action("typing", Some(name), _, Some(true)) =>
      board ! Board.StartEdit(name)
    case Action("typing", _, _, Some(false)) =>
      board ! Board.StopEdit
    case Board.Messages(messages) =>
      for {
        message <- messages
      } {
        val action = Action("message", Some(message.name), Some(message.text), None)
        import model.ActionJson._
        channel.push(Json.toJson(action))
      }
    case Board.Editors(editors) =>
      val editorString = editors.mkString(", ")
      val action = Action("editors", None, Some(editorString), None)
      import model.ActionJson._
      channel.push(Json.toJson(action))
    case User.ClientConnectionLost =>
      board ! Board.Unsubscribe
      self ! PoisonPill
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