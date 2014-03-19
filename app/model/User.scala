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
    case () =>
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