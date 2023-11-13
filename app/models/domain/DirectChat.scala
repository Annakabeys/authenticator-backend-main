package models.domain

import java.util.UUID
import play.api.libs.json._

case class DirectChat (
  id: UUID,
  sender: String,
  receiver: String
)

object DirectChat {
  val tupled = (apply: (UUID, String, String) => DirectChat).tupled
  def apply (sender: String, receiver: String): DirectChat = apply(UUID.randomUUID(), sender, receiver)
  implicit val writes: Writes[DirectChat] = Json.writes[DirectChat]
  def unapply(chat: DirectChat): Option[(UUID, String, String)] = Some((chat.id, chat.sender, chat.receiver))
}