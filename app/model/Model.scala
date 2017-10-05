package model

import play.api.libs.json.{JsPath, Reads}
import play.api.libs.functional.syntax._

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

case class GitHubUser(userId: String, userLogin: String, userAvatar: String)

object GitHubUser {

  implicit val gitHubUserReads: Reads[GitHubUser] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "login").read[String] and
      (JsPath \ "avatar_url").read[String]
    ) (GitHubUser.apply _)
}

case class ReviewComment(commentBody: String, user: GitHubUser)

object ReviewComment {

  implicit val commentReads: Reads[ReviewComment] = (
    (JsPath \ "body").read[String] and
      (JsPath \ "user").read[GitHubUser]
    ) (ReviewComment.apply _)
}
