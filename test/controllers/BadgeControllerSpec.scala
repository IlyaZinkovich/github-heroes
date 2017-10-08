package controllers

import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers._

class BadgeControllerSpec extends PlaySpec with TestAppConfig {

  "BadgeController" should {

    "return a list of badges received by user with specified login" in {
      val receiverLogin = "IlyaZinkovich"
      val badgesResponse = route(app, FakeRequest(GET, s"/badges?receiverLogin=$receiverLogin"))
        .get

      status(badgesResponse) mustBe OK
      contentType(badgesResponse) mustBe Some("application/json")
    }
  }
}
