package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent._
import security.SecureAction
import security.UserRequest

@Singleton
class SecureActionController @Inject()(
  secureAction: SecureAction,
  val cc: ControllerComponents
)(implicit val ec: ExecutionContext) extends AbstractController(cc) {

  def checkAuth() = secureAction.async { implicit request =>
   request.session.get("username") match {
     case Some(username) => Future.successful(Ok(username))
     case None => Future.successful(Unauthorized)
   }
  }
}
