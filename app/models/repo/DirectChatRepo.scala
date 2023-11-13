package models.repo

import javax.inject._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext
import java.util.UUID
import models.domain.DirectChat

@Singleton
final class DirectChatRepo @Inject()(
  val userRepo: UserRepo,
  protected val dbConfigProvider: DatabaseConfigProvider,
  implicit val ec: ExecutionContext
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import slick.jdbc.PostgresProfile.api._

  protected class DirectChatTable(tag: Tag) extends Table[DirectChat](tag, "DIRECT_CHATS") {
    def id = column[UUID]("ID", O.PrimaryKey)
    def sender = column[String]("SENDER")
    def receiver = column[String]("RECEIVER")

    def * = (id, sender, receiver).mapTo[DirectChat]
    def senderForeignKey = foreignKey("sender_FK", sender, userRepo.users)(_.username, onDelete = ForeignKeyAction.Cascade)
  }

  val directChats = TableQuery[DirectChatTable]

  def createDirectChatTable() = db.run(directChats.schema.createIfNotExists)

  def findDirectChatById(id: UUID) = db.run(directChats.filter(_.id === id).result.headOption)

  def getDirectChats() = db.run(directChats.result)

  def addDirectChat(directChat: DirectChat) = db.run(directChats += directChat)

}
