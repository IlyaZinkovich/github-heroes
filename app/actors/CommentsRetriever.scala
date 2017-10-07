package actors

import actors.PullRequestActionHandler.CommentsRetrieved
import akka.actor.{Actor, Props}
import model.{Comment, PullRequestAction}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

class CommentsRetriever(client: WSClient) extends Actor {

  import CommentsRetriever._

  private implicit val ec = context.dispatcher

  def receive = {
    case Retrieve(pullRequestAction) =>
      val originalSender = sender()
      client.url(pullRequestAction.pullRequest.commentsUrl).get()
        .map(commentsResponse => Json.parse(commentsResponse.body).as[Seq[Comment]])
        .foreach(comments => originalSender ! CommentsRetrieved(pullRequestAction, comments))
  }
}

object CommentsRetriever {

  def props(client: WSClient): Props = Props(new CommentsRetriever(client))

  case class Retrieve(pullRequestAction: PullRequestAction)

}
