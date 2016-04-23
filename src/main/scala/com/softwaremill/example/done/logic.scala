package com.softwaremill.example.done

import com.softwaremill.events.{AggregateForEvent, CommandResult, Event, EventListener, ModelUpdate}
import com.softwaremill.example.{EmailService, Troll, TrollModel}
import slick.dbio.DBIOAction

import scala.concurrent.ExecutionContext

case class TrollAddedDone(name: String)

object TrollAddedDone {
  implicit val afe = AggregateForEvent[TrollAddedDone, Troll]
}

case class AxeEquippedDone()

object AxeEquippedDone {
  implicit val afe = AggregateForEvent[AxeEquippedDone, Troll]
}

class CommandsDone(trollModel: TrollModel)(implicit ec: ExecutionContext) {
  def addTroll(name: String): CommandResult[String, Unit] = {
    trollModel.findByName(name).flatMap {
      case Some(_) => CommandResult.failed("Troll already exists!")
      case None =>
        val event = Event(TrollAddedDone(name)).forNewAggregate
        CommandResult.successful((), event)
    }
  }
}

class EventListenersDone(implicit ec: ExecutionContext) {
  val notifyTrollOversightCouncil: EventListener[TrollAddedDone] = e => DBIOAction.from {
    EmailService.sendEmail("A new troll")
  }.map(_ => Nil)

  val equipWithAxe: EventListener[TrollAddedDone] = { e =>
    val event = Event(AxeEquippedDone()).forAggregate(e.aggregateId)
    DBIOAction.successful(List(event))
  }
}

class ModelUpdatesDone(trollModel: TrollModel) {
  val addedUpdate: ModelUpdate[TrollAddedDone] = e => trollModel.updateNew(Troll(e.aggregateId, e.data.name, None))

  val equipmentUpdate: ModelUpdate[AxeEquippedDone] = e => trollModel.updateEquipment(e.aggregateId, "axe")
}
