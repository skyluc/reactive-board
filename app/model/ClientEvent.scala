package model

sealed trait ClientEvent

case class InitEvent(id: String) extends ClientEvent
case class NewMessageEvent(name: String, text: String) extends ClientEvent
case class EditFocusEvent(name: String) extends ClientEvent
case class EditUnfocusEvent(name: String) extends ClientEvent
case class EditorListEvent(editors: String) extends ClientEvent

