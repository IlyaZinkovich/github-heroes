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

  private val config = Configuration.from(
    Map(
      "dynamo.endpoint" -> dynamoEndpoint,
      "dynamo.region" -> dynamoRegion
    )
  )

  "BadgesDynamoRepository" should {

    "persist badges" in {
      val from = GitHubUser(1, "1", "1.img")
      val to = GitHubUser(2, "IlyaZinkovich", "2.img")
      val repo = GitHubRepo(1, "repo", "http://repo.url", 1, 2, 3)
      val badgeName = "awesome"
      val badgeImageUrl = "http://img.png"
      val timestamp = Instant.now()

      val badgeRepository = new BadgeDynamoRepository(config)
      badgeRepository.persist(Badge(badgeName, badgeImageUrl, from, to, timestamp, repo))
    }

    "query badges by receiver" in {
      new BadgeDynamoRepository(config).findBadgesReceivedByUser("IlyaZinkovich")
    }
  }

}
