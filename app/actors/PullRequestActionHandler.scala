package actors

import actors.CommentsRetriever.Retrieve
import actors.HeroBadgePersister.{HeroBadge, PersistHeroBadge}
import actors.HeroCommentsDetector.DetectHeroComment
import akka.actor.{Actor, Props}
import model.{Comment, PullRequest, PullRequestAction}
import play.api.Logger
import play.api.libs.ws.WSClient

class PullRequestActionHandler(client: WSClient) extends Actor {

  import PullRequestActionHandler._

  private val commentsRetriever = context.actorOf(CommentsRetriever.props(client), "comments-retriever")
  private val heroCommentsDetector = context.actorOf(HeroCommentsDetector.props, "hero-comments-detector")
  private val heroBadgePersister = context.actorOf(HeroBadgePersister.props, "hero-badge-persister")

  def receive = {
    case HandlePullRequestAction(pullRequestAction) =>
      Logger.debug(sender().toString())
      commentsRetriever ! Retrieve(pullRequestAction.pullRequest)
    case CommentsRetrieved(pullRequest, comments) =>
      heroCommentsDetector ! DetectHeroComment(pullRequest, comments)
    case HeroComment(pullRequest, comment) =>
      heroBadgePersister ! PersistHeroBadge(HeroBadge("regular", 10, "/"), pullRequest.user, pullRequest.user)
  }
}

object PullRequestActionHandler {

  def props(client: WSClient): Props = Props(new PullRequestActionHandler(client))

  case class HandlePullRequestAction(pullRequestAction: PullRequestAction)

  case class CommentsRetrieved(pullRequest: PullRequest, comments: Seq[Comment])

  case class HeroComment(pullRequest: PullRequest, comment: Comment)

}
