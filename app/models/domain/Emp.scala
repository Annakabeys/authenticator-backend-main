package models.domain

import java.util.UUID
import play.api.libs.json._

case class Emp (
  id: UUID,
  firstName: String,
  middleName: Option[String],
  lastName: String,
  username: String,
  password: String,
  role: String
)

object Emp {
  val tupled = (apply: (UUID, String, Option[String], String, String, String, String) => Emp).tupled
  def apply (firstName: String, middleName: Option[String], lastName: String,username: String, password: String, role: String): Emp = apply(UUID.randomUUID(), firstName, middleName, lastName, username, password, role)
  implicit val writes: Writes[Emp] = (emp: Emp) => {
    Json.obj(
      "id" -> emp.id.toString,
      "firstName" -> emp.firstName,
      "middleName" -> emp.middleName,
      "lastName" -> emp.lastName,
      "username" -> emp.username,
      "role" -> emp.role
    )
  }
  def unapply(emp: Emp): Option[(UUID, String, Option[String], String, String, String, String)] = Some((emp.id, emp.firstName, emp.middleName, emp.lastName, emp.username, emp.password, emp.role))
}