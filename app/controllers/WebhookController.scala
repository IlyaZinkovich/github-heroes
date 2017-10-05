package controllers

import javax.inject.{Inject, Singleton}

import akka.util.Timeout
import model.PullRequestAction._
import model.ReviewComment._
import model.{PullRequestAction, ReviewComment}
import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebhookController @Inject()(cc: ControllerComponents, client: WSClient)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {

  private implicit val timeout: Timeout = 5.seconds

  def webhook() = Action.async { implicit request: Request[AnyContent] =>
    request.body.asJson.map(json => json.as[PullRequestAction]) match {
      case Some(pullRequestAction) => client.url(pullRequestAction.pullRequest.commentsUrl).get()
        .map(reviewComments => {
          val heroComment = Json.parse(reviewComments.body).as[Seq[ReviewComment]]
            .filter(comment => comment.user == pullRequestAction.pullRequest.user)
            .filter(comment => comment.commentBody.startsWith("hero"))
            .map(comment => comment.commentBody).mkString
          Logger.debug(heroComment)
          Ok
        })
      case None => Future(NoContent)
    }
  }
}
