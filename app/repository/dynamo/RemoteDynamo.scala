package repository.dynamo

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import play.api.Configuration

trait RemoteDynamo extends Dynamo {

  def getDynamoDB(configuration: Configuration): DynamoDB = {
    val region = configuration.get[String]("dynamo.region")
    val id = configuration.get[String]("aws.id")
    val secret = configuration.get[String]("aws.secret")

    val client = AmazonDynamoDBClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(id, secret)))
      .withRegion(region)
      .build()

    new DynamoDB(client)
  }
}