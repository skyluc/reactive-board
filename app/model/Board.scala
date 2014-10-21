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
import play.api.Application

object Board {
  
  case object LatestMessages
  case class Messages(messages: List[Message])
  case class AddMessage(message: Message)
    
  def getLatestMessages(implicit app: Application): Future[List[Message]] = {
    implicit val timeout = Timeout(5.seconds)
    
    val actorReply = Actors.board ? LatestMessages

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
  
  def addMessage(message: Message)(implicit app: Application) {
    Actors.board ! AddMessage(message)
  }

  def props = Props[Board]

}

class Board extends Actor {
  
  import Board._
  
  var messages = List[Message]()
  
  override def receive = {
    case LatestMessages =>
      sender ! Messages(messages.take(10).reverse)
    case AddMessage(message) =>
      messages = message :: messages
  }
}
