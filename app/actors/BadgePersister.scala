package actors

import akka.actor.{Actor, Props}
import model.Badge
import repository.BadgeRepository

class BadgePersister(badgeRepository: BadgeRepository) extends Actor {

  import actors.BadgePersister._

  def receive = {
    case PersistBadge(badge) => badgeRepository.persist(badge)
  }
}

object BadgePersister {

  def props(badgeRepository: BadgeRepository): Props = Props(new BadgePersister(badgeRepository))

  case class PersistBadge(badge: Badge)

}
