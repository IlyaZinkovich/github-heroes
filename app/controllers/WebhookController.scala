package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc._

@Singleton
class WebhookController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def webhook() = Action { implicit request: Request[AnyContent] =>
    Ok
  }
}
