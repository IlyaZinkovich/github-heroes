package actors

import akka.actor.{Actor, Props}
import model.Badge
import play.api.Logger

class BadgePersister extends Actor {

  import actors.BadgePersister._

  def receive = {
    case PersistBadge(badge) => Logger.debug(s"Persist $badge")
  }
}

object BadgePersister {

  def props: Props = Props(new BadgePersister)

  case class PersistBadge(badge: Badge)

}
