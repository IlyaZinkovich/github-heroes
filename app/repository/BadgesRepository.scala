package repository

import java.time.Instant

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import model.{Badge, GitHubUser, Repository}

object BadgesRepository {

  private val client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
    new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "eu-central-1"))
    .build()
  private val dynamoDB = new DynamoDB(client)

  def persist(badge: Badge): Unit = {
    val badgesTable = dynamoDB.getTable("Badges")
    val item = new Item().withPrimaryKey("ID", badge.timestamp.toString)
      .withString("badge_name", badge.name)
      .withString("badge_url", badge.imageUrl)
    badgesTable.putItem(item)
  }

  def main(args: Array[String]): Unit = {
    val from = GitHubUser(1, "1", "1.img")
    val to = GitHubUser(2, "2", "2.img")
    val repo = Repository(1, "repo", "http://repo.url", 1, 2, 3)
    val badgeName = "regular"
    val badgeImageUrl = "http://img.png"
    val timestamp = Instant.now()
    persist(Badge(badgeName, badgeImageUrl, from, to, timestamp, repo))
  }
}
