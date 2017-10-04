package controllers

import javax.inject.{Inject, Singleton}

import akka.util.Timeout
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebhookController @Inject()(cc: ControllerComponents, client: WSClient)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {

  private implicit val timeout: Timeout = 5.seconds

  def webhook() = Action.async { implicit request: Request[AnyContent] =>
    request.body.asJson.map(json => (json \ "pull_request" \ "comments_url").as[String]) match {
      case Some(commentsUrl) => client.url(commentsUrl).get().map(reviewComments => {
        val heroComment = Json.parse(reviewComments.body).as[Seq[JsValue]]
          .filter(comment => (comment \ "user" \ "id").as[String] == "testUserId")
          .filter(comment => (comment \ "body").as[String].startsWith("hero"))
          .map(comment => (comment \ "body").as[String]).mkString
        Logger.debug(heroComment)
        Ok
      })
      case None => Future(NoContent)
    }
  }
}
