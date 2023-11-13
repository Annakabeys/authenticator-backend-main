package models.domain

import java.util.UUID
import models.domain.User
import play.api.libs.json._

case class DirectMessages (
  id: UUID, 
  directChatId: UUID,
  sender: String,
  receiver: String,
  content: String,
  timestamp: Long,
)

object DirectMessages {
  val tupled = (apply: (UUID, UUID, String, String, String, Long) => DirectMessages).tupled
  def apply(directChatId: UUID, sender: String, receiver: String, content: String, timestamp: Long): DirectMessages = apply(UUID.randomUUID(), directChatId, sender, receiver, content, timestamp)
  implicit val writes: Writes[DirectMessages] = Json.writes[DirectMessages]
  def unapply(message: DirectMessages): Option[(UUID, UUID, String, String, String, Long)] = Some((message.id, message.directChatId, message.sender, message.receiver, message.content, message.timestamp))
}