package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.repo.EmpRepo
import scala.concurrent._
import play.api.libs.json._
import models.domain.Emp
import play.api.data.Form
import play.api.data.Forms._
import java.util.UUID
import security.SecureAction
import security.UserRequest

@Singleton
class HomeController @Inject()(
  secureAction: SecureAction,
  val empRepo: EmpRepo,
  val cc: ControllerComponents
)(implicit val ec: ExecutionContext) extends AbstractController(cc) {

  val empForm = Form(
    mapping(
      "id" -> ignored(UUID.randomUUID()),
      "firstName" -> nonEmptyText,
      "middleName" -> optional(text),
      "lastName" -> nonEmptyText,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "role" -> nonEmptyText,
    )(Emp.apply)(Emp.unapply)
  )

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
    )
  )

  def index() = Action.async { implicit request: Request[AnyContent] =>
    empRepo.createEmployeeTable().map { _ => Ok("Employee table created!")}
  }

  def getAllEmployees() = Action.async { implicit request: Request[AnyContent] =>
    empRepo.getAllEmployees().map { employees => Ok(Json.toJson(employees))}
  }

  // def addEmployee() = authenticator.async { implicit request: Request[AnyContent] =>
  //   empForm.bindFromRequest().fold(
  //     errors => {
  //       Future.successful(BadRequest)
  //     },
  //     employee => {
  //       empRepo.addEmployee(employee.copy(id = UUID.randomUUID())).map { _ => Ok("Employee added!")}
  //     }
  //   )
  // }

  def addEmployee() = secureAction.async { implicit request: Request[AnyContent] =>
    request.session.get("username").map { username =>
      empForm.bindFromRequest().fold(
        errors => {
          Future.successful(BadRequest)
        },
        employee => {
          empRepo.addEmployee(employee.copy(id = UUID.randomUUID())).map { _ =>
            Ok("Employee added!")
          }
        }
      )
    }.getOrElse {
      Future.successful(Unauthorized("User not authenticated"))
    }
  }

  def login() = Action.async { implicit request: Request[AnyContent] =>
    loginForm.bindFromRequest().fold(
      error => {
        Future.successful(Unauthorized)
      },
      credentials => {
        empRepo.findEmployeeByUsernameAndPassword(credentials._1, credentials._2).map { employee =>
          employee match {
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
