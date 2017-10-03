package controllers

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

class WebhookControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "WebhookController" should {

    "react on closed pull requests" in {
      val action = "closed"
      val isMerged = true
      val userId = "testUserId"
      val userLogin = "testUserLogin"
      val userAvatar = "https://avatars3.githubusercontent.com/u/0000000?v=4"
      val contentTypeHeader = ("Content", "application/json")

      val webhookResponse = route(app, FakeRequest(POST, "/")
        .withHeaders(contentTypeHeader)
        .withJsonBody(buildPullRequestWebhookPayload(action, isMerged, userId, userLogin, userAvatar)))
        .get

      status(webhookResponse) mustBe OK
    }

  }

  private def buildPullRequestWebhookPayload(action: String,
                                             isMerged: Boolean,
                                             testUserId: String,
                                             testUserLogin: String,
                                             testUserAvatar: String) = {
    val testUser = Json.obj(
      "id" -> testUserId,
      "login" -> testUserLogin,
      "testUserAvatarUrl" -> testUserAvatar
    )

    Json.obj(
      "action" -> action,
      "pull_request" -> Json.obj("merged" -> isMerged, "merged_by" -> testUser)
    )
  }
}
