package security

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent._

class UserRequest[A](val username: Option[String], request: Request[A]) extends WrappedRequest[A](request)

class SecureAction @Inject() (val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[UserRequest, AnyContent]
    with ActionTransformer[Request, UserRequest] {
  def transform[A](request: Request[A]) = Future.successful {
    new UserRequest(request.session.get("username"), request)
  }
}

// trait UserComponent {
//     def username: String
//     def password: String
// }

// case class User(
//     val username: String,
//     val password: String) extends UserComponent

// class UserRequest[A](val user: Option[User], request: Request[A]) extends WrappedRequest[A](request)

// // class UserAction @Inject() (val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
// //     extends ActionBuilder[UserRequest, AnyContent]
// //     with ActionTransformer[Request, UserRequest] {
// //   def transform[A](request: Request[A]) = Future.successful {
// //     new UserRequest(request.session.get("username"), request)
// //   }
// // }

// @Singleton
// class Authenticator @Inject() (defaultParser: BodyParsers.Default)(implicit ec: ExecutionContext)
//     extends ActionBuilder[UserRequest, AnyContent] {
//     val logger = Logger(this.getClass)

//     def parser: play.api.mvc.BodyParser[play.api.mvc.AnyContent] = defaultParser

//     protected def executionContext: scala.concurrent.ExecutionContext = ec
//     override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
//         logger.info("Calling action")
//         request.session.get("username") match {
//             case Some(username) =>
//                 block(new UserRequest(Some(User(username, "admin")), request))
//             case None =>
//                 block(new UserRequest(None, request))
//         }
//     }
// }