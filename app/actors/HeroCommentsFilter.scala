package actors

import akka.actor.{Actor, Props}
import model.{Comment, PullRequest}

class HeroCommentsFilter extends Actor {

  import HeroCommentsFilter._

  def receive = {
    case Filter(pullRequest, comments) => comments.filter(comment => comment.user == pullRequest.user)
      .filter(comment => comment.commentBody.startsWith("hero"))
  }

}

object HeroCommentsFilter {

  def props: Props = Props(new HeroCommentsFilter)

  case class Filter(pullRequest: PullRequest, comments: Seq[Comment])
}


