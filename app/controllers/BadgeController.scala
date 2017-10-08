package controllers

import javax.inject.{Inject, Singleton}

import model.Badge
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import repository.BadgeRepository
import Badge._

@Singleton
class BadgeController @Inject()(cc: ControllerComponents,
                                badgeRepository: BadgeRepository) extends AbstractController(cc) {

  def findBadges(receiverLogin: String) = Action { implicit request: Request[AnyContent] =>
    val badges: Seq[Badge] = badgeRepository.findBadgesReceivedByUser(receiverLogin)
    Ok(Json.toJson(badges))
  }
}
