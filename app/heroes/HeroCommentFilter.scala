package heroes

import model.{PullRequest, Comment}

object HeroCommentFilter {

  def userMatches(pullRequest: PullRequest, comment: Comment): Boolean = {
    comment.user == pullRequest.user
  }

  def comment(comment: Comment): Boolean = {
    comment.commentBody.startsWith("hero")
  }
}
