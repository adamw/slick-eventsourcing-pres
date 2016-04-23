package com.softwaremill.example.done

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.softwaremill.events.{EventMachine, EventsDatabase, HandleContext}
import com.softwaremill.example.TeslaOrderModel

class RoutesDone(
  eventsDatabase: EventsDatabase,
  eventMachine: EventMachine,
  teslaOrderModel: TeslaOrderModel,
  commands: CommandsDone) {

  implicit val hc = HandleContext.System

  val routes = path("place_order") {
    post {
      entity(as[String]) { name =>
        onSuccess(eventMachine.run(commands.placeOrder(name))) {
          case Left(error) => complete(StatusCodes.BadRequest, error)
          case Right(()) => complete("ok")
        }
      }
    }
  } ~ path("list_orders") {
    get {
      onSuccess(eventsDatabase.db.run(teslaOrderModel.findAll())) { orders =>
        complete {
          orders.map(o => o.name + o.equipment.fold("")(e => s", equipped with $e")).mkString("\n")
        }
      }
    }
  }
}
