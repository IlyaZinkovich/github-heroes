package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Injecting}

class BadgeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

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
