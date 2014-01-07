package controllers

import play.api.GlobalSettings
import play.libs.Akka
import model.Board
import model.Message

object Global extends GlobalSettings {
  
  override def onStart(app: play.api.Application) {
    initializeActors()
  } 
  
  def initializeActors() {
    val system = Akka.system
    
    val board = system.actorOf(Board.props, "board")
    
    // add some random data
    board ! Board.AddMessage(Message("SkyLuc", "Hello, there!"))
    board ! Board.AddMessage(Message("TROLL", "Ur wAPP !PRETTY"))
  }

}