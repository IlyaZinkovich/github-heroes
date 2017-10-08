import com.google.inject.AbstractModule
import play.api.Mode.Test
import play.api.{Configuration, Environment}
import repository.BadgeRepository
import repository.dynamo.{BadgeDynamoRepository, LocalDynamo, RemoteDynamo}

class Module(environment: Environment, configuration: Configuration) extends AbstractModule {

  override def configure() = {
    environment.mode match {
      case Test => bind(classOf[BadgeRepository])
        .toInstance(new BadgeDynamoRepository(configuration) with LocalDynamo)
      case _ => bind(classOf[BadgeRepository])
        .toInstance(new BadgeDynamoRepository(configuration) with RemoteDynamo)
    }
  }
}
