package com.softwaremill.example

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.softwaremill.events.{EventMachine, EventsDatabase, HandleContext}

class Routes() {

  implicit val hc = HandleContext.System

  val routes = path("place_order") {
    post {
      entity(as[String]) { name =>
        complete {
          "ko"
        }
      }
    }
  } ~ path("list_orders") {
    get {
      complete {
        "ko"
      }
    }
  }
}
