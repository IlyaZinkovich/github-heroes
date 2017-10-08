package repository.dynamo

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import play.api.Configuration

trait LocalDynamo extends Dynamo {

  def getDynamoDB(configuration: Configuration): DynamoDB = {
    val endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
      configuration.get[String]("dynamo.endpoint"),
      configuration.get[String]("dynamo.region")
    )

    val client = AmazonDynamoDBClientBuilder.standard()
      .withEndpointConfiguration(endpointConfiguration)
      .build()

    new DynamoDB(client)
  }
}
