package repository

import java.time.Instant

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, PrimaryKey}
import model.{Badge, GitHubRepo, GitHubUser}
import org.scalatest.WordSpecLike
import play.api.Configuration
import repository.dynamo.BadgeDynamoRepository

class BadgesDynamoRepositoryITSpec extends WordSpecLike {

  private val dynamoEndpoint = "http://localhost:8000"
  private val dynamoRegion = "eu-central-1"
  private val endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(dynamoEndpoint, dynamoRegion)
  private val client = AmazonDynamoDBClientBuilder.standard()
    .withEndpointConfiguration(endpointConfiguration)
    .build()
  private val dynamoDB = new DynamoDB(client)
  private val badgesTable = dynamoDB.getTable("Badges")

  "BadgesDynamoRepository" should {

    "persist badges" in {
      val from = GitHubUser(1, "1", "1.img")
      val to = GitHubUser(2, "2", "2.img")
      val repo = GitHubRepo(1, "repo", "http://repo.url", 1, 2, 3)
      val badgeName = "awesome"
      val badgeImageUrl = "http://img.png"
      val timestamp = Instant.now()

      val config = Configuration.from(
        Map(
          "dynamo.endpoint" -> dynamoEndpoint,
          "dynamo.region" -> dynamoRegion
        )
      )

      new BadgeDynamoRepository(config).persist(Badge(badgeName, badgeImageUrl, from, to, timestamp, repo))

      val item = badgesTable.getItem(new PrimaryKey("ID", timestamp.toString))

      assert(item.get("badge_name") == badgeName)
    }

  }

}
