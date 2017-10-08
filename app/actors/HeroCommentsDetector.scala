package actors

import actors.PullRequestActionHandler.HeroComment
import akka.actor.{Actor, Props}
import model.{Comment, PullRequest, PullRequestAction}

class HeroCommentsDetector extends Actor {

  import HeroCommentsDetector._

  def receive = {
    case DetectHeroComment(pullRequestAction, comments) =>
      val originalSender = sender()
      comments.filter(userMatchesReviewer(_, pullRequestAction.pullRequest))
        .find(commentStartsWithHero)
        .foreach(heroComment => originalSender ! HeroComment(pullRequestAction, heroComment))
  }

  def userMatchesReviewer(comment: Comment, pullRequest: PullRequest): Boolean = {
    pullRequest.mergedBy match {
      case Some(mergedByUser) => comment.user == mergedByUser
      case _ => false
    }
  }

  def commentStartsWithHero(comment: Comment): Boolean = {
    comment.commentBody.startsWith("hero")
  }
}

object HeroCommentsDetector {

  def props: Props = Props(new HeroCommentsDetector)

  case class DetectHeroComment(pullRequestAction: PullRequestAction, comments: Seq[Comment])

}
