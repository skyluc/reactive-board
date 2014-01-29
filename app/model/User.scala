package model

import akka.actor.Actor
import play.api.libs.iteratee.Concurrent
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.ActorSelection
import views.html.message
import akka.actor.PoisonPill

class User(channel: Concurrent.Channel[String], board: ActorSelection) extends Actor {

  def receive = {
    case "go:" =>
      board ! Board.SubscribeAndGetAll
    case User.messageRegex(user, text) =>
      board ! Board.AddMessage(Message(user, text))
    case User.ClientConnectionLost =>
      board ! Board.Unsubscribe
      self ! PoisonPill
    case Board.Messages(messages) =>
      messages.foreach { m =>
        channel.push(message(m).body)
      }
  }

  override def postStop() {
    channel.eofAndEnd()
  }

}

object User {

  val messageRegex = "msg:([^:]*):(.*)".r
  object ClientConnectionLost

  def props(channel: Concurrent.Channel[String], board: ActorSelection) =
    Props(classOf[User], channel, board)

}