package model

import scala.concurrent.Future
import play.libs.Akka
import scala.concurrent.Promise
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef

object Board {

  case object LatestMessages
  case class Messages(messages: List[Message])
  case class AddMessage(message: Message)
  case object SubscribeAndGetAll
  case object Unsubscribe

  def getLatestMessages: Future[List[Message]] = {
    val system = Akka.system
    val board = system.actorSelection("user/board")

    implicit val timeout = Timeout(5.seconds)
    val actorReply = board ? LatestMessages

    val promise = Promise[List[Message]]

    actorReply.onComplete {
      case Failure(e) =>
        promise.failure(e)
      case Success(Messages(messages)) =>
        promise.complete(Success(messages))
      case Success(o) =>
        promise.failure(new Exception(s"unexpected reply: $o"))
    }

    promise.future
  }
  
  def addMessage(message: Message) {
    val system = Akka.system
    val board = system.actorSelection("user/board")
    
    board ! AddMessage(message)
  }

  def props = Props[Board]

}

class Board extends Actor {
  
  import Board._
  
  var messages = List[Message]()
  var users = Set[ActorRef]()
  
  override def receive = {
    case LatestMessages =>
      sender ! Messages(messages.take(10).reverse)
    case AddMessage(message) =>
      messages = message :: messages
      users.foreach(_ ! Messages(List(message)))
    case SubscribeAndGetAll =>
      users += sender
      sender ! Messages(messages.take(10).reverse)
    case Unsubscribe =>
      users -= sender
  }
}