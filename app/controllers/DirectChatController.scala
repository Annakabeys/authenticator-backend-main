package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.repo.DirectChatRepo
import scala.concurrent._
import play.api.libs.json._
import models.domain.DirectChat
import play.api.data.Form
import play.api.data.Forms._
import java.util.UUID
import security.SecureAction
import security.UserRequest

@Singleton
class DirectChatController @Inject()(
  secureAction: SecureAction,
  val directChatRepo: DirectChatRepo,
  val cc: ControllerComponents
)(implicit val ec: ExecutionContext) extends AbstractController(cc) {

  val directChatForm = Form(
    tuple(
      "id" -> ignored(UUID.randomUUID()),
      "receiver" -> nonEmptyText
    )
  )

  def addDirectChat() = secureAction.async { implicit request: Request[AnyContent] =>
    request.session.get("username").map { username =>
      directChatForm.bindFromRequest().fold(
        errors => {
          Future.successful(BadRequest)
        },
        directChat => {
          val receiver = directChat(1)

          directChatRepo.addDirectChat(DirectChat(UUID.randomUUID(), username, receiver)).map { _ =>
            Ok("Direct chat added!")
          }
        }
      )
    }.getOrElse {
      Future.successful(Unauthorized("User not authenticated"))
    }
  }
}
