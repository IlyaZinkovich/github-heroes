package repository.dynamo

import com.amazonaws.services.dynamodbv2.document.DynamoDB
import play.api.Configuration

trait Dynamo {

  def getDynamoDB(configuration: Configuration): DynamoDB
}
