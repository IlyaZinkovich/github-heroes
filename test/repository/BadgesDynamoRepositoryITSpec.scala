package repository

import java.time.Instant

import model.{Badge, GitHubRepo, GitHubUser}
import org.scalatest.WordSpecLike
import play.api.Configuration
import repository.dynamo.{BadgeDynamoRepository, LocalDynamo}

class BadgesDynamoRepositoryITSpec extends WordSpecLike {

  private val dynamoEndpoint = "http://localhost:8000"
  private val dynamoRegion = "eu-central-1"

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

      val badgeRepository = new BadgeDynamoRepository(config) with LocalDynamo
      badgeRepository.persist(Badge(badgeName, badgeImageUrl, from, to, timestamp, repo))
    }

    "query badges by receiver" in {
      val repository = new BadgeDynamoRepository(config) with LocalDynamo

      repository.findBadgesReceivedByUser("IlyaZinkovich")
    }
  }

}
