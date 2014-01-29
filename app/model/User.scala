package model

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSelection
import views.html.message

class User(channel: Concurrent.Channel[String], board: ActorSelection) extends Actor {
  
  def receive = {
    case "go:" =>
      board ! Board.RegisterUser(self)
    case User.messageRegex(user, text) =>
      board ! Board.AddMessage(Message(user, text))
    case Board.Messages(messages) =>
      messages.foreach{m =>
        channel.push(message(m).body)
      }
  }

}

object User {
  
  val messageRegex = "msg:([^:]*):(.*)".r
  
  def props(channel: Concurrent.Channel[String], board: ActorSelection) =
    Props(classOf[User], channel, board)
  
}