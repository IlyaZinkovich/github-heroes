package actors

import actors.PullRequestActionHandler.HeroComment
import akka.actor.{Actor, Props}
import model.{Comment, PullRequest}

class HeroCommentsDetector extends Actor {

  import HeroCommentsDetector._

  def receive = {
    case DetectHeroComment(pullRequest, comments) =>
      val originalSender = sender()
      comments.filter(userMatchesReviewer(_, pullRequest))
        .find(commentStartsWithHero)
        .foreach(heroComment => originalSender ! HeroComment(pullRequest, heroComment))
  }

  def userMatchesReviewer(comment: Comment, pullRequest: PullRequest): Boolean = {
    comment.user == pullRequest.user
  }

  def commentStartsWithHero(comment: Comment): Boolean = {
    comment.commentBody.startsWith("hero")
  }
}

object HeroCommentsDetector {

  def props: Props = Props(new HeroCommentsDetector)

  case class DetectHeroComment(pullRequest: PullRequest, comments: Seq[Comment])

}
