package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.repo.UserRepo
import models.repo.DirectChatRepo
import scala.concurrent._
import play.api.libs.json._
import models.domain.User
import play.api.data.Form
import play.api.data.Forms._
import java.util.UUID
import security.SecureAction
import security.UserRequest

@Singleton
class HomeController @Inject()(
  secureAction: SecureAction,
  val userRepo: UserRepo,
  val directChatRepo: DirectChatRepo,
  val cc: ControllerComponents
)(implicit val ec: ExecutionContext) extends AbstractController(cc) {

  val userForm = Form(
    mapping(
      "id" -> ignored(UUID.randomUUID()),
      "firstName" -> nonEmptyText,
      "middleName" -> optional(text),
      "lastName" -> nonEmptyText,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )(User.apply)(User.unapply)
  )

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )
  )

  def index() = Action.async { implicit request: Request[AnyContent] =>
    for {
      _ <- userRepo.createUserTable()
      _ <- directChatRepo.createDirectChatTable()
    } yield Ok("Tables created!")
  }

  def getAllUsers() = Action.async { implicit request: Request[AnyContent] =>
    userRepo.getAllUsers().map { users => Ok(Json.toJson(users))}
  }

  def addUser() = Action.async { implicit request: Request[AnyContent] =>
    userForm.bindFromRequest().fold(
      errors => {
        Future.successful(BadRequest)
      },
      user => {
        userRepo.addUser(user.copy(id = UUID.randomUUID())).map { _ => Ok("User added!")}
      }
    )
  }

  // def addEmployee() = secureAction.async { implicit request: Request[AnyContent] =>
  //   request.session.get("username").map { username =>
  //     userForm.bindFromRequest().fold(
  //       errors => {
  //         Future.successful(BadRequest)
  //       },
  //       employee => {
  //         userRepo.addEmployee(employee.copy(id = UUID.randomUUID())).map { _ =>
  //           Ok("Employee added!")
  //         }
  //       }
  //     )
  //   }.getOrElse {
  //     Future.successful(Unauthorized("User not authenticated"))
  //   }
  // }

  def login() = Action.async { implicit request: Request[AnyContent] =>
    loginForm.bindFromRequest().fold(
      error => {
        Future.successful(Unauthorized)
      },
      credentials => {
        userRepo.findUserByUsernameAndPassword(credentials(0), credentials(1)).map { user =>
          user match {
            case Some(em) =>
              Ok(Json.toJson(em)).withSession("username" -> em.username)
            case None => Unauthorized
          }
        }
      }
    )
  }

  def logout() = secureAction.async { implicit request =>
    Future.successful(Ok("Logged out!").withNewSession)
  }
}
