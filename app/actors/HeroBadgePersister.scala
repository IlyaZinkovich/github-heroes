package actors

import akka.actor.{Actor, Props}
import model.GitHubUser
import play.api.Logger

class HeroBadgePersister extends Actor {

  import actors.HeroBadgePersister._

  def receive = {
    case PersistHeroBadge(heroBadge, from, to) => Logger.debug(s"Persist $heroBadge from $from to $to")
  }
}

object HeroBadgePersister {

  def props: Props = Props(new HeroBadgePersister)

  case class PersistHeroBadge(heroBadge: HeroBadge, from: GitHubUser, to: GitHubUser)

  case class HeroBadge(name: String, value: Int, imageUrl: String)

}
