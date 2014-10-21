package model

import play.api.Application
import play.api.Plugin
import play.api.libs.concurrent.Akka
import akka.actor.ActorRef
import play.api.Play

/**
 * Lookup for actors used by the web front end.
 */
object Actors {

  private def actors(implicit app: Application): Actors = app.plugin[Actors]
    .getOrElse(sys.error("Actors plugin not registered"))

  /**
   * Get the board.
   */
  def board(implicit app: Application) = actors.board
}

/**
 * Manages the creation of actors in the web front end.
 *
 * This is discovered by Play in the `play.plugins` file.
 */
class Actors(app: Application) extends Plugin {

  private def system = Akka.system(app)

  override def onStart() = {
    val board = system.actorOf(Board.props, "board")
    
    board ! Board.AddMessage(Message("SkyLuc", "Hello, there!"))
    board ! Board.AddMessage(Message("TROLL", "Ur wAPP !PRETTY"))
  }

  private lazy val board = system.actorSelection(Play.current.configuration.getString("board.controller.path").get)
}