package com.softwaremill.example

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.softwaremill.events.{EventMachine, EventsDatabase, HandleContext}

class Routes(
  eventsDatabase: EventsDatabase,
  eventMachine: EventMachine,
  trollModel: TrollModel,
  commands: Commands) {

  implicit val hc = HandleContext.System

  val routes = path("add_troll") {
    post {
      entity(as[String]) { name =>
        onSuccess(eventMachine.run(commands.addTroll(name))) {
          case Left(error) => complete(StatusCodes.BadRequest, error)
          case Right(()) => complete("ok")
        }
      }
    }
  } ~ path("list_trolls") {
    get {
      onSuccess(eventsDatabase.db.run(trollModel.findAll())) { trolls =>
        complete {
          trolls.map(t => t.name + t.equipment.fold("")(e => s", equipped with $e")).mkString("\n")
        }
      }
    }
  }
}
