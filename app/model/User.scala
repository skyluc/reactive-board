package model

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSelection
import views.html.message

class User(channel: Concurrent.Channel[String], board: ActorSelection) extends Actor {
  
  def receive = {
    
    case User.messageRegex("go", _) =>
      board ! Board.RegisterUser(self)
    case Board.Messages(messages) =>
      messages.foreach{m =>
        channel.push(message(m).body)
      }
  }

}

object User {
  
  val messageRegex = "([^:]*):(.*)".r
  
  def props(channel: Concurrent.Channel[String], board: ActorSelection) =
    Props(classOf[User], channel, board)
  
}