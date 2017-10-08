package controllers

import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._

class HomeControllerSpec extends PlaySpec with TestAppConfig {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
    }
  }
}
