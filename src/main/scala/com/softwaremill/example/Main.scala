package com.softwaremill.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.softwaremill.events.{EventsDatabase, EventsModule, Registry}
import com.softwaremill.id.DefaultIdGenerator
import com.typesafe.scalalogging.StrictLogging
import org.flywaydb.core.Flyway

import scala.util.{Failure, Success}

object Main extends App with StrictLogging with EventsModule {
  implicit lazy val _system = ActorSystem("slick-eventsourcing")
  implicit lazy val _materializer = ActorMaterializer()
  implicit lazy val ec = _system.dispatcher

  val dbUrl = "jdbc:h2:file:./data/slickeventsourcing"

  SchemaUpdate.update(dbUrl)

  lazy val idGenerator = new DefaultIdGenerator(datacenterId = 1)

  // ---

  lazy val eventsDatabase = EventsDatabase.createH2(dbUrl)
  lazy val teslaOrderModel = new TeslaOrderModel(eventsDatabase)

  lazy val routes = new Routes()

  lazy val registry = Registry()

  // ---

  lazy val routesWithIndex = routes.routes ~
      path("") {
        getFromResource("index.html")
      }

  Http()
    .bindAndHandle(routesWithIndex, "localhost", 8080)
    .onComplete {
      case Success(b) => logger.info(s"Server started")
      case Failure(e) => logger.error(s"Cannot start server", e)
    }
}

object SchemaUpdate {
  def update(connectionString: String) {
    val flyway = new Flyway()
    flyway.setDataSource(connectionString, "", "")
    flyway.migrate()
  }
}