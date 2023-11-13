// package controllers

// import javax.inject._
// import play.api._
// import play.api.mvc._
// import models.repo.DirectMessagesRepo
// import scala.concurrent._
// import play.api.libs.json._
// import models.domain.DirectMessages
// import play.api.data.Form
// import play.api.data.Forms._
// import java.util.UUID
// import security.SecureAction
// import security.UserRequest

// @Singleton
// class DirectMessagesController @Inject()(
//   secureAction: SecureAction,
//   val directMessagesRepo: DirectMessagesRepo,
//   val cc: ControllerComponents
// )(implicit val ec: ExecutionContext) extends AbstractController(cc) {

//   val directMessagesForm = Form(
//     single(
//       "content" -> nonEmptyText
//     )
//   )
  
//   def addDirectChatMessage(directChatId: String) = secureAction.async { implicit request: UserRequest[AnyContent] =>
//     request.session.get("username").map { username =>
//       directMessagesForm.bindFromRequest().fold(
//         errors => {
//           Future.successful(BadRequest)
//         },
//         directMessage => {
//           val chatId = UUID.fromString(directChatId)
//           val receiver = ""

//           val directChatMessage = DirectMessages(UUID.randomUUID(), chatId, username, receiver, directMessage, System.currentTimeMillis())

//           directMessagesRepo.addDirectMessage(directChatMessage).map { _ =>
//             Ok("Direct chat message added!")
//           }
//         }
//       )
//     }.getOrElse {
//       Future.successful(Unauthorized("User not authenticated"))
//     }
//   }
// }