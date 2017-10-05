package controllers

import javax.inject.{Inject, Singleton}

import actors.CommentsRetriever
import actors.CommentsRetriever.Retrieve
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import model.PullRequestAction._
import model.{PullRequestAction, Comment}
import play.api.Logger
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebhookController @Inject()(cc: ControllerComponents,
                                  client: WSClient,
                                  actorSystem: ActorSystem)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {

  private implicit val timeout: Timeout = 1.second

  private val commentsRetriever = actorSystem.actorOf(CommentsRetriever.props(client), "comments-retriever")

  def webhook() = Action.async { implicit request: Request[AnyContent] =>
    request.body.asJson.map(json => json.as[PullRequestAction]) match {
      case Some(pullRequestAction) =>
        (commentsRetriever ? Retrieve(pullRequestAction.pullRequest.commentsUrl))
          .mapTo[Seq[Comment]]
          .map(comments => {
            val commentString = comments.filter(comment => comment.user == pullRequestAction.pullRequest.user)
              .filter(comment => comment.commentBody.startsWith("hero"))
              .map(_.commentBody).mkString
            Logger.debug(commentString)
            Ok
          })
      case None => Future(NoContent)
    }
  }
}
