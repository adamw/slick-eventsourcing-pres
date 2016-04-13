package com.softwaremill.example

import com.softwaremill.events.{AggregateForEvent, CommandResult, Event, EventListener, ModelUpdate}
import slick.dbio.DBIOAction

import scala.concurrent.ExecutionContext

case class TrollAdded(name: String)
object TrollAdded {
  implicit val afe = AggregateForEvent[TrollAdded, Troll]
}

case class AxeEquipped()
object AxeEquipped {
  implicit val afe = AggregateForEvent[AxeEquipped, Troll]
}

class Commands(trollModel: TrollModel)(implicit ec: ExecutionContext) {
  def addTroll(name: String): CommandResult[String, Unit] = {
    trollModel.findByName(name).flatMap {
      case Some(_) => CommandResult.failed("Troll already exists!")
      case None =>
        val event = Event(TrollAdded(name)).forNewAggregate
        CommandResult.successful((), event)
    }
  }
}

class EventListeners(implicit ec: ExecutionContext) {
  val notifyTrollOversightCouncil: EventListener[TrollAdded] = e => DBIOAction.from {
    EmailService.sendEmail("A new troll")
  }.map(_ => Nil)

  val equipWithAxe: EventListener[TrollAdded] = { e =>
    val event = Event(AxeEquipped()).forAggregate(e.aggregateId)
    DBIOAction.successful(List(event))
  }
}

class ModelUpdates(trollModel: TrollModel) {
  val addedUpdate: ModelUpdate[TrollAdded] = e => trollModel.updateNew(Troll(e.aggregateId, e.data.name, None))

  val equipmentUpdate: ModelUpdate[AxeEquipped] = e => trollModel.updateEquipment(e.aggregateId, "axe")
}
