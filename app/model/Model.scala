package model

import java.time.Instant

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}

case class PullRequestAction(action: String, pullRequest: PullRequest)

object PullRequestAction {

  implicit val pullRequestActionReads: Reads[PullRequestAction] = (
    (JsPath \ "action").read[String] and
      (JsPath \ "pull_request").read[PullRequest]
    ) (PullRequestAction.apply _)
}

case class PullRequest(isMerged: Boolean, commentsUrl: String, user: GitHubUser)

object PullRequest {

  implicit val pullRequestReads: Reads[PullRequest] = (
    (JsPath \ "merged").read[Boolean] and
      (JsPath \ "comments_url").read[String] and
      (JsPath \ "merged_by").read[GitHubUser]
    ) (PullRequest.apply _)
}

case class GitHubUser(userId: Int, userLogin: String, userAvatar: String)

object GitHubUser {

  implicit val gitHubUserReads: Reads[GitHubUser] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "login").read[String] and
      (JsPath \ "avatar_url").read[String]
    ) (GitHubUser.apply _)
}

case class Comment(commentBody: String, user: GitHubUser)

object Comment {

  implicit val commentReads: Reads[Comment] = (
    (JsPath \ "body").read[String] and
      (JsPath \ "user").read[GitHubUser]
    ) (Comment.apply _)
}

case class Badge(name: String, imageUrl: String, from: GitHubUser, to: GitHubUser,
                 timestamp: Instant, repository: Repository)

case class Repository(id: Int, name: String, url: String,
                      starsCount: Int, forksCount: Int, watchersCount: Int)
