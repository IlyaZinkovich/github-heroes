package repository

import model.Badge

trait BadgeRepository {

  def persist(badge: Badge)
}
