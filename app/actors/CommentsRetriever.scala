package actors

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import model.ReviewComment
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

class CommentsRetriever(client: WSClient) extends Actor {

  import CommentsRetriever._

  private implicit val ec = context.dispatcher

  def receive = {
    case Retrieve(commentsUrl) => client.url(commentsUrl).get()
      .map(comments => Json.parse(comments.body).as[Seq[ReviewComment]]) pipeTo sender
  }
}

object CommentsRetriever {

  def props(client: WSClient): Props = Props(new CommentsRetriever(client))

  case class Retrieve(commentsUrl: String)

}
