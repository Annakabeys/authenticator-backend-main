package models.repo

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext
import java.util.UUID
import models.domain.User

@Singleton
final class UserRepo @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider,
  implicit val ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.jdbc.PostgresProfile.api._

  protected class UserTable(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[UUID]("ID", O.PrimaryKey)
    def firstName = column[String]("FIRST_NAME")
    def middleName = column[Option[String]]("MIDDLE_NAME")
    def lastName = column[String]("LAST_NAME")
    def username = column[String]("USERNAME", O.Unique)
    def password = column[String]("PASSWORD")

    def * = (id, firstName, middleName, lastName, username, password).mapTo[User]
  }

  val users = TableQuery[UserTable]

  def createUserTable() = db.run(users.schema.createIfNotExists)

  def getAllUsers() = db.run(users.result)

  def addUser(user: User) = db.run(users += user)

  def findUserByUsernameAndPassword(username: String, password: String) = db.run(users.filter(u => u.username === username && u.password === password).result.headOption)
}

