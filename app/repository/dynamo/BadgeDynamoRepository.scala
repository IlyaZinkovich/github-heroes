package repository.dynamo

import java.time.Instant
import javax.inject.{Inject, Singleton}

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item, QueryOutcome}
import model.{Badge, GitHubRepo, GitHubUser}
import play.api.Configuration
import repository.BadgeRepository

@Singleton
class BadgeDynamoRepository @Inject()(configuration: Configuration) extends BadgeRepository {

  private val endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
    configuration.get[String]("dynamo.endpoint"),
    configuration.get[String]("dynamo.region")
  )

  private val client = AmazonDynamoDBClientBuilder.standard()
    .withEndpointConfiguration(endpointConfiguration)
    .build()

  private val dynamoDB = new DynamoDB(client)

  def persist(badge: Badge): Unit = {
    val badgesTable = dynamoDB.getTable("github-badges")

    val item = badgeToItem(badge)

    badgesTable.putItem(item)
  }

  def findBadgesReceivedByUser(gitHubUserLogin: String): Seq[Badge] = {
    val badgesTable = dynamoDB.getTable("github-badges")

    val spec = new QuerySpec().withKeyConditionExpression("to_github_user_login = :receiverLogin")
      .withValueMap(new ValueMap().withString(":receiverLogin", gitHubUserLogin))

    val items = badgesTable.query(spec)

    getAllItems(items.iterator(), List())
  }

  private def getAllItems(iterator: IteratorSupport[Item, QueryOutcome],
                          badges: List[Badge]): Seq[Badge] = {
    if (iterator.hasNext) {
      val item = iterator.next()
      val badge: Badge = itemToBadge(item)
      getAllItems(iterator, badge :: badges)
    } else {
      badges
    }
  }

  private def badgeToItem(badge: Badge) = {
    new Item()
      .withString("badge_name", badge.name)
      .withString("badge_image_url", badge.imageUrl)
      .withNumber("from_github_user_id", badge.from.userId)
      .withString("from_github_user_login", badge.from.userLogin)
      .withString("from_github_user_avatar_url", badge.from.userAvatarUrl)
      .withNumber("to_github_user_id", badge.to.userId)
      .withString("to_github_user_login", badge.to.userLogin)
      .withString("to_github_user_avatar_url", badge.to.userAvatarUrl)
      .withString("timestamp", badge.timestamp.toString)
      .withNumber("repository_id", badge.repository.id)
      .withString("repository_name", badge.repository.name)
      .withString("repository_url", badge.repository.url)
      .withNumber("repository_stars_count", badge.repository.starsCount)
      .withNumber("repository_forks_count", badge.repository.starsCount)
      .withNumber("repository_watchers_count", badge.repository.starsCount)
  }

  private def itemToBadge(item: Item) = {
    val badgeName = item.getString("badge_name")
    val badgeImageUrl = item.getString("badge_image_url")
    val fromGitHubUserId = item.getNumber("from_github_user_id").intValue()
    val fromGitHubUserLogin = item.getString("from_github_user_login")
    val fromGitHubUserAvatarUrl = item.getString("from_github_user_avatar_url")
    val toGitHubUserId = item.getNumber("to_github_user_id").intValue()
    val toGitHubUserLogin = item.getString("to_github_user_login")
    val toGitHubUserAvatarUrl = item.getString("to_github_user_avatar_url")
    val timestamp = Instant.parse(item.getString("timestamp"))
    val repositoryId = item.getNumber("repository_id").intValue()
    val repositoryName = item.getString("repository_name")
    val repositoryUrl = item.getString("repository_url")
    val repositoryStars = item.getNumber("repository_stars_count").intValue()
    val respositoryForks = item.getNumber("repository_forks_count").intValue()
    val respositoryWatches = item.getNumber("repository_watchers_count").intValue()
    val from = new GitHubUser(fromGitHubUserId, fromGitHubUserLogin, fromGitHubUserAvatarUrl)
    val to = new GitHubUser(toGitHubUserId, toGitHubUserLogin, toGitHubUserAvatarUrl)
    val repo = new GitHubRepo(repositoryId, repositoryName, repositoryUrl,
      repositoryStars, respositoryForks, respositoryWatches)
    val badge = Badge(badgeName, badgeImageUrl, from, to, timestamp, repo)
    badge
  }
}
