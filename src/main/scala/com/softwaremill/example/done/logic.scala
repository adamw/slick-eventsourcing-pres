package com.softwaremill.example.done

import com.softwaremill.events.{AggregateForEvent, CommandResult, Event, EventListener, ModelUpdate}
import com.softwaremill.example.{EmailService, TeslaOrder, TeslaOrderModel}
import slick.dbio.DBIOAction

import scala.concurrent.ExecutionContext

case class TeslaOrderedDone(name: String)

object TeslaOrderedDone {
  implicit val afe = AggregateForEvent[TeslaOrderedDone, TeslaOrder]
}

case class ExtraEquipmentAddedDone(what: String)

object ExtraEquipmentAddedDone {
  implicit val afe = AggregateForEvent[ExtraEquipmentAddedDone, TeslaOrder]
}

class CommandsDone(teslaOrderModel: TeslaOrderModel)(implicit ec: ExecutionContext) {
  def placeOrder(name: String): CommandResult[String, Unit] = {
    teslaOrderModel.findByName(name).flatMap {
      case Some(_) => CommandResult.failed("You've already ordered one!")
      case None =>
        val event = Event(TeslaOrderedDone(name)).forNewAggregate
        CommandResult.successful((), event)
    }
  }
}

class EventListenersDone(implicit ec: ExecutionContext) {
  val sendCustomerNotification: EventListener[TeslaOrderedDone] = e => DBIOAction.from {
    EmailService.sendEmail("Your Tesla model 3 will be ready in a couple of years")
  }.map(_ => Nil)

  val addExtraEquipment: EventListener[TeslaOrderedDone] = { e =>
    val event = Event(ExtraEquipmentAddedDone("Battery pack")).forAggregate(e.aggregateId)
    DBIOAction.successful(List(event))
  }
}

class ModelUpdatesDone(teslaOrderModel: TeslaOrderModel) {
  val orderedUpdated: ModelUpdate[TeslaOrderedDone] = e =>
    teslaOrderModel.updateNew(TeslaOrder(e.aggregateId, e.data.name, None))

  val equipmentUpdate: ModelUpdate[ExtraEquipmentAddedDone] = e =>
    teslaOrderModel.updateEquipment(e.aggregateId, e.data.what)
}
