package controllers

import javax.inject.{Inject, Singleton}

import actors.PullRequestActionHandler
import actors.PullRequestActionHandler.HandlePullRequestAction
import akka.actor.ActorSystem
import akka.util.Timeout
import model.PullRequestAction
import model.PullRequestAction._
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Singleton
class WebhookController @Inject()(cc: ControllerComponents,
                                  client: WSClient,
                                  actorSystem: ActorSystem)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {

  private implicit val timeout: Timeout = 20.second

  private val pullRequestActionHandler = actorSystem.actorOf(PullRequestActionHandler.props(client), "pr-handler")

  def webhook() = Action { implicit request: Request[AnyContent] =>
    request.body.asJson.map(json => json.as[PullRequestAction]) match {
      case Some(pullRequestAction) =>
        pullRequestActionHandler ! HandlePullRequestAction(pullRequestAction)
        Ok
      case None => NoContent
    }
  }
}
