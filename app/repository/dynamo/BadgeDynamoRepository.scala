package repository.dynamo

import javax.inject.{Inject, Singleton}

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import model.Badge
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
    val badgesTable = dynamoDB.getTable("Badges")

    val item = new Item().withPrimaryKey("ID", badge.timestamp.toString)
      .withString("badge_name", badge.name)
      .withString("badge_image_url", badge.imageUrl)
      .withNumber("from_github_user_id", badge.from.userId)
      .withString("from_github_user_login", badge.from.userLogin)
      .withString("from_github_user_avatar_url", badge.from.userAvatarUrl)
      .withNumber("to_github_user_id", badge.to.userId)
      .withString("to_github_user_login", badge.to.userLogin)
      .withString("to_github_user_avatar_url", badge.to.userAvatarUrl)
      .withString("timestamp", badge.imageUrl)
      .withNumber("repository_id", badge.repository.id)
      .withString("repository_name", badge.repository.name)
      .withString("repository_url", badge.repository.url)
      .withNumber("repository_stars_count", badge.repository.starsCount)
      .withNumber("repository_forks_count", badge.repository.starsCount)
      .withNumber("repository_watchers_count", badge.repository.starsCount)

    badgesTable.putItem(item)
  }
}
