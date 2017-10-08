package model

import java.time.Instant

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

case class PullRequestAction(action: String, pullRequest: PullRequest, repo: GitHubRepo)

object PullRequestAction {

  implicit val pullRequestActionReads: Reads[PullRequestAction] = (
    (JsPath \ "action").read[String] and
      (JsPath \ "pull_request").read[PullRequest] and
      (JsPath \ "repository").read[GitHubRepo]
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

case class GitHubRepo(id: Int, name: String, url: String,
                      starsCount: Int, forksCount: Int, watchersCount: Int)

object GitHubRepo {

  implicit val githubRepoReads: Reads[GitHubRepo] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "full_name").read[String] and
      (JsPath \ "html_url").read[String] and
      (JsPath \ "stargazers_count").read[Int] and
      (JsPath \ "forks").read[Int] and
      (JsPath \ "watchers").read[Int]
    ) (GitHubRepo.apply _)

  implicit val gitHubRepoWrites: Writes[GitHubRepo] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "full_name").write[String] and
      (JsPath \ "html_url").write[String] and
      (JsPath \ "stargazers_count").write[Int] and
      (JsPath \ "forks").write[Int] and
      (JsPath \ "watchers").write[Int]
    ) (unlift(GitHubRepo.unapply))
}

case class GitHubUser(userId: Int, userLogin: String, userAvatarUrl: String)

object GitHubUser {

  implicit val gitHubUserReads: Reads[GitHubUser] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "login").read[String] and
      (JsPath \ "avatar_url").read[String]
    ) (GitHubUser.apply _)

  implicit val gitHubUserWrites: Writes[GitHubUser] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "login").write[String] and
      (JsPath \ "avatar_url").write[String]
    ) (unlift(GitHubUser.unapply))
}

case class Comment(commentBody: String, user: GitHubUser)

object Comment {

  implicit val commentReads: Reads[Comment] = (
    (JsPath \ "body").read[String] and
      (JsPath \ "user").read[GitHubUser]
    ) (Comment.apply _)
}

case class Badge(name: String, imageUrl: String, from: GitHubUser, to: GitHubUser,
                 timestamp: Instant, repository: GitHubRepo)

object Badge {

  implicit val badgeWrites: Writes[Badge] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "image_url").write[String] and
      (JsPath \ "from").write[GitHubUser] and
      (JsPath \ "to").write[GitHubUser] and
      (JsPath \ "timestamp").write[Instant] and
      (JsPath \ "repository").write[GitHubRepo]
    ) (unlift(Badge.unapply))
}
