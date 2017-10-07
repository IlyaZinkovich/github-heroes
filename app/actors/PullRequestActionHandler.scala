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
      commentsRetriever ! Retrieve(pullRequestAction.pullRequest)
    case CommentsRetrieved(pullRequest, comments) =>
      heroCommentsDetector ! DetectHeroComment(pullRequest, comments)
    case HeroComment(pullRequest, comment) =>
      val from = pullRequest.user
      val to = pullRequest.user
      val repo = Repository(1, "repo", "http://repo.url", 1, 2, 3)
      val badgeName = "regular"
      val badgeImageUrl = "http://img.png"
      val timestamp = Instant.now()
      badgePersister ! PersistBadge(Badge(badgeName, badgeImageUrl, from, to, timestamp, repo))
  }
}

object PullRequestActionHandler {

  def props(client: WSClient, badgeRepository: BadgeRepository): Props =
    Props(new PullRequestActionHandler(client, badgeRepository))

  case class HandlePullRequestAction(pullRequestAction: PullRequestAction)

  case class CommentsRetrieved(pullRequest: PullRequest, comments: Seq[Comment])

  case class HeroComment(pullRequest: PullRequest, comment: Comment)

}
