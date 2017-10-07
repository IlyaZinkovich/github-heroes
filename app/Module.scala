import com.google.inject.AbstractModule
import repository.BadgeRepository
import repository.dynamo.BadgeDynamoRepository

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[BadgeRepository])
      .to(classOf[BadgeDynamoRepository])
  }
}
