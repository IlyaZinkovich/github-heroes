package repository

import model.Badge

trait BadgeRepository {

  def persist(badge: Badge): Unit

  def findBadgesReceivedByUser(gitHubUserLogin: String): Seq[Badge]
}
