package actors

import actors.CommentsRetriever.Retrieve
import akka.actor.{Actor, Props}
import model.{Comment, PullRequest, PullRequestAction}
import play.api.libs.ws.WSClient

class PullRequestActionHandler(client: WSClient) extends Actor {

  import PullRequestActionHandler._

  private val commentsRetriever = context.actorOf(CommentsRetriever.props(client), "comments-retriever")

  def receive = {
    case Handle(pullRequestAction) => commentsRetriever ! Retrieve(pullRequestAction.pullRequest)
    case CommentsRetrieved(pullRequest, comments) =>

  }
}

object PullRequestActionHandler {

  def props(client: WSClient): Props = Props(new PullRequestActionHandler(client))

  case class Handle(pullRequestAction: PullRequestAction)
  case class CommentsRetrieved(pullRequest: PullRequest, comments: Seq[Comment])
}
