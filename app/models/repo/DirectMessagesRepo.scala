// package models.repo

// import javax.inject._
// import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
// import slick.jdbc.JdbcProfile
// import scala.concurrent.ExecutionContext
// import java.util.UUID
// import models.domain.DirectMessages

// @Singleton
// final class DirectMessagesRepo @Inject()(
//   val directChatRepo: DirectChatRepo,
//   protected val dbConfigProvider: DatabaseConfigProvider,
//   implicit val ec: ExecutionContext
// ) extends HasDatabaseConfigProvider[JdbcProfile] {

//   import slick.jdbc.PostgresProfile.api._

//   protected class DirectMessagesTable(tag: Tag) extends Table[DirectMessages](tag, "DIRECT_MESSAGES") {
//     def id = column[UUID]("ID", O.PrimaryKey)
//     def directChatId = column[UUID]("DIRECT_CHAT_ID", O.Unique)
//     def sender = column[String]("SENDER")
//     def receiver = column[String]("RECEIVER")
//     def content = column[String]("CONTENT")
//     def timestamp = column[Long]("TIMESTAMP")

//     def * = (id, directChatId, sender, receiver, content, timestamp).mapTo[DirectMessages]
//     def fk1 = foreignKey("direct_chat_FK", directChatId, directChatRepo.directChats)(_.id, onDelete = ForeignKeyAction.Cascade)
//     def fk2 = foreignKey("sender_FK", sender, directChatRepo.directChats)(_.sender, onDelete = ForeignKeyAction.Cascade)
//     def fk3 = foreignKey("receiver_FK", receiver, directChatRepo.directChats)(_.receiver, onDelete = ForeignKeyAction.Cascade)
//   }

//   val directMessages = TableQuery[DirectMessagesTable]

//   def createDirectMessagesTable() = db.run(directMessages.schema.createIfNotExists)

//   def findDirectMessagesById(directChatId: UUID) = db.run(directMessages.filter(_.directChatId === directChatId).result.headOption)

//   def addDirectMessage(directChatMessage: DirectMessages) = db.run(directMessages += directChatMessage)
// }