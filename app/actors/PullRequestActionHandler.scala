package actors

import java.time.Instant

import actors.BadgePersister.PersistBadge
import actors.CommentsRetriever.Retrieve
import actors.HeroCommentsDetector.DetectHeroComment
import akka.actor.{Actor, Props}
import model._
import play.api.libs.ws.WSClient
import repository.BadgeRepository

class PullRequestActionHandler(client: WSClient, badgeRepository: BadgeRepository) extends Actor {

  import PullRequestActionHandler._

  private val commentsRetriever = context.actorOf(CommentsRetriever.props(client), "comments-retriever")
  private val heroCommentsDetector = context.actorOf(HeroCommentsDetector.props, "hero-comments-detector")
  private val badgePersister = context.actorOf(BadgePersister.props(badgeRepository), "badge-persister")

  def receive = {
    case HandlePullRequestAction(pullRequestAction) =>
      if (pullRequestAction.action == "closed") commentsRetriever ! Retrieve(pullRequestAction)
    case CommentsRetrieved(pullRequestAction, comments) =>
      heroCommentsDetector ! DetectHeroComment(pullRequestAction, comments)
    case HeroComment(pullRequestAction, comment) =>
      val from = comment.user
      val to = pullRequestAction.pullRequest.requestedBy
      val repo = pullRequestAction.repo
      val badgeName = comment.commentBody.replaceFirst("hero ", "")
      val badgeImageUrl = ""
      val timestamp = Instant.now()
      badgePersister ! PersistBadge(Badge(badgeName, badgeImageUrl, from, to, timestamp, repo))
  }
}

object PullRequestActionHandler {

  def props(client: WSClient, badgeRepository: BadgeRepository): Props =
    Props(new PullRequestActionHandler(client, badgeRepository))

  case class HandlePullRequestAction(pullRequestAction: PullRequestAction)

  case class CommentsRetrieved(pullRequestAction: PullRequestAction, comments: Seq[Comment])

  case class HeroComment(pullRequestAction: PullRequestAction, comment: Comment)

}
