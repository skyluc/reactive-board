package model

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Action(id: String, name: Option[String], text: Option[String], typing: Option[Boolean])

case class ActionExt(id: String, content: List[String])

object ActionJson {

  implicit val messageReads: Reads[Action] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "name").readNullable[String] and
    (JsPath \ "text").readNullable[String] and
    (JsPath \ "focus").readNullable[Boolean])(Action.apply _)

  implicit val actionWrites: Writes[Action] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "name").writeNullable[String] and
    (JsPath \ "text").writeNullable[String] and
    (JsPath \ "focus").writeNullable[Boolean])(unlift(Action.unapply))

  implicit val actionExt: Writes[ActionExt] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "content").write[List[String]])(unlift(ActionExt.unapply))
}