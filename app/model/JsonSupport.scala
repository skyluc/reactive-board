package model

import play.api.libs.json._
import play.api.mvc.WebSocket.FrameFormatter
import play.api.libs.functional.syntax._

object JsonSupport {

  implicit def clientEventFormat: Format[ClientEvent] = Format(
    (__ \ "event").read[String].flatMap {
      case "init" => initEventFormat.map(identity)
      case "new-message" => newMessageEventFormat.map(identity)
      case "edit-focus" => editFocusEventFormat.map(identity)
      case "edit-unfocus" => editUnfocusEventFormat.map(identity)
      case "editor-list" => editorListEventFormat.map(identity)
      case other => Reads(_ => JsError("Unknown client event: " + other))
    },
    Writes {
      case i: InitEvent => initEventFormat.writes(i)
      case nm: NewMessageEvent => newMessageEventFormat.writes(nm)
      case ef: EditFocusEvent => editFocusEventFormat.writes(ef)
      case eu: EditUnfocusEvent => editUnfocusEventFormat.writes(eu)
      case el: EditorListEvent => editorListEventFormat.writes(el)
    }
  )
  
  implicit def initEventFormat: Format[InitEvent] = (
    (__ \ "event").format[String] ~
      (__ \ "id").format[String]
    ).apply({
    case ("init", id) => InitEvent(id)
  }, initEvent => ("init", initEvent.id))
  
  implicit def newMessageEventFormat: Format[NewMessageEvent] = (
    (__ \ "event").format[String] ~
      (__ \ "name").format[String] ~
      (__ \ "text").format[String]
    ).apply({
    case ("new-message", name, text) => NewMessageEvent(name, text)
  }, newMessageEvent => ("new-message", newMessageEvent.name, newMessageEvent.text))
  
  implicit def editFocusEventFormat: Format[EditFocusEvent] = (
    (__ \ "event").format[String] ~
      (__ \ "name").format[String]
    ).apply({
    case ("edit-focus", name) => EditFocusEvent(name)
  }, editFocusEvent => ("edit-focus", editFocusEvent.name))
  
  implicit def editUnfocusEventFormat: Format[EditUnfocusEvent] = (
    (__ \ "event").format[String] ~
      (__ \ "name").format[String]
    ).apply({
    case ("edit-unfocus", name) => EditUnfocusEvent(name)
  }, editUnfocusEvent => ("edit-unfocus", editUnfocusEvent.name))
  
  implicit def editorListEventFormat: Format[EditorListEvent] = (
    (__ \ "event").format[String] ~
      (__ \ "editors").format[String]
    ).apply({
    case ("editor-list", name) => EditorListEvent(name)
  }, editorListEvent => ("editor-list", editorListEvent.editors))
  
  /**
   * Formats WebSocket frames to be ClientEvents.
   */
  implicit def clientEventFrameFormatter: FrameFormatter[ClientEvent] = FrameFormatter.jsonFrame.transform(
    clientEvent => Json.toJson(clientEvent),
    json => Json.fromJson[ClientEvent](json).fold(
      invalid => throw new RuntimeException("Bad client event on WebSocket: " + invalid),
      valid => valid
    )
  )
  
  
}