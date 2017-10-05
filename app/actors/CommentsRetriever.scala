package actors

import actors.PullRequestActionHandler.CommentsRetrieved
import akka.actor.{Actor, Props}
import akka.pattern.pipe
import model.{Comment, PullRequest, PullRequestAction}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

class CommentsRetriever(client: WSClient) extends Actor {

  import CommentsRetriever._

  private implicit val ec = context.dispatcher

  def receive = {
    case Retrieve(pullRequest) => client.url(pullRequest.commentsUrl).get()
      .map(commentsResponse => Json.parse(commentsResponse.body).as[Seq[Comment]])
      .foreach(comments => sender ! CommentsRetrieved(pullRequest, comments))
  }
}

object CommentsRetriever {

  def props(client: WSClient): Props = Props(new CommentsRetriever(client))

  case class Retrieve(pullRequest: PullRequest)

}
