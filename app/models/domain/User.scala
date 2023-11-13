package models.domain

import java.util.UUID
import play.api.libs.json._

case class User (
  id: UUID,
  firstName: String,
  middleName: Option[String],
  lastName: String,
  username: String,
  password: String
)

object User {
  val tupled = (apply: (UUID, String, Option[String], String, String, String) => User).tupled
  def apply (firstName: String, middleName: Option[String], lastName: String,username: String, password: String): User = apply(UUID.randomUUID(), firstName, middleName, lastName, username, password)
  implicit val writes: Writes[User] = (user: User) => {
    Json.obj(
      "id" -> user.id.toString,
      "firstName" -> user.firstName,
      "middleName" -> user.middleName,
      "lastName" -> user.lastName,
      "username" -> user.username,
    )
  }
  def unapply(user: User): Option[(UUID, String, Option[String], String, String, String)] = Some((user.id, user.firstName, user.middleName, user.lastName, user.username, user.password))
}