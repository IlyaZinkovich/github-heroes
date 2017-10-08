package controllers

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Mode}

trait TestAppConfig {

  val app: Application = new GuiceApplicationBuilder()
    .in(Mode.Test)
    .build()
}
