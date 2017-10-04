package controllers

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlEqualTo}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._

class WebhookControllerSpec extends PlaySpec with GuiceOneAppPerTest with BeforeAndAfterEach {

  val wireMockHost = "localhost"
  val wireMockPort = 8888
  val wireMockServer = new WireMockServer(wireMockConfig.port(wireMockPort))

  override def beforeEach {
    wireMockServer.start()
    WireMock.configureFor(wireMockHost, wireMockPort)
  }

  override def afterEach {
    wireMockServer.stop()
  }

  "WebhookController" should {

    "react on closed pull requests" in {
      val action = "closed"
      val isMerged = true
      val userId = "testUserId"
      val userLogin = "testUserLogin"
      val userAvatar = "https://avatars3.githubusercontent.com/u/0000000?v=4"
      val contentTypeHeader = ("Content", "application/json")
      val commentsPath = "repos/IlyaZinkovich/heroes-check/issues/1/comments"
      val commentsUrl = s"http://$wireMockHost:$wireMockPort/$commentsPath"
      val gitHubUserJson = gitHubUser(userId, userLogin, userAvatar)
      val commentBody = "comment body"
      val heroCommentBody = "hero 10"
      val otherUserId = "anotherTestUserId"
      val otherUserLogin = "anotherTestUserLogin"
      val otherUserAvatar = "https://avatars3.githubusercontent.com/u/1111111?v=4"
      val regularComment = reviewComment(gitHubUser(userId, userLogin, userAvatar), commentBody)
      val heroComment = reviewComment(gitHubUser(userId, userLogin, userAvatar), heroCommentBody)
      val otherUserComment = reviewComment(gitHubUser(otherUserId, otherUserLogin, otherUserAvatar), commentBody)
      val fakeHeroComment = reviewComment(gitHubUser(otherUserId, otherUserLogin, otherUserAvatar), heroCommentBody)

      stubFor(get(urlEqualTo(s"/$commentsPath"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(Json.stringify(JsArray(List(regularComment, heroComment, otherUserComment, fakeHeroComment))))
        )
      )

      val webhookResponse = route(app, FakeRequest(POST, "/")
        .withHeaders(contentTypeHeader)
        .withJsonBody(buildPullRequestWebhookPayload(action, isMerged, gitHubUserJson, commentsUrl)))
        .get

      status(webhookResponse) mustBe OK
    }

  }

  private def reviewComment(gitHubUser: JsValue, commentBody: String) = {
    Json.obj(
      "user" -> gitHubUser,
      "body" -> commentBody
    )
  }

  private def buildPullRequestWebhookPayload(action: String,
                                             isMerged: Boolean,
                                             gitHubUser: JsValue,
                                             commentsUrl: String) = {
    Json.obj(
      "action" -> action,
      "pull_request" -> Json.obj(
        "merged" -> isMerged,
        "merged_by" -> gitHubUser,
        "comments_url" -> commentsUrl
      )
    )
  }

  private def gitHubUser(testUserId: String, testUserLogin: String, testUserAvatar: String) = {
    Json.obj(
      "id" -> testUserId,
      "login" -> testUserLogin,
      "testUserAvatarUrl" -> testUserAvatar
    )
  }
}
