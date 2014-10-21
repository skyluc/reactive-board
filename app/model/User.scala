package model

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.ActorSelection

class User(upstream: ActorRef, board: ActorSelection) extends Actor {
  
  def receive = {
    case InitEvent(_) =>
      board ! Board.SubscribeAndGetAll
      
    case NewMessageEvent(name, text) =>
      board ! Board.AddMessage(new Message(name, text))
      
    case EditFocusEvent(name) =>
      board ! Board.StartEdit(name)
      
    case EditUnfocusEvent(name) =>
      board ! Board.StopEdit(name)
      
    case Board.Messages(messages) =>
      messages.foreach { m =>
        upstream ! NewMessageEvent(m.name, m.text)
      }
    
    case Board.Editors(editors) =>
      upstream ! EditorListEvent(editors.mkString(", "))
  }

}

object User {
  def props(upstream: ActorRef, board: ActorSelection) = Props(classOf[User], upstream, board)
}