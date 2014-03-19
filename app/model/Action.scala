package model

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Action(id: String, name: Option[String], data: Option[String], typing: Option[Boolean])

object ActionJson {

  implicit val messageReads: Reads[Action] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "data").readNullable[String] and
    (JsPath \ "focus").readNullable[Boolean])(Action.apply _)

  implicit val actionWrites: Writes[Action] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "name").writeNullable[String] and
    (JsPath \ "data").writeNullable[String] and
    (JsPath \ "focus").writeNullable[Boolean])(unlift(Action.unapply))

}