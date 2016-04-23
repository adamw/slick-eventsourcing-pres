package com.softwaremill.example

import com.softwaremill.events.EventsDatabase
import slick.dbio.Effect.{Write, Read}

import scala.concurrent.ExecutionContext

class TeslaOrderModel(protected val database: EventsDatabase)(implicit val ec: ExecutionContext) {

  import database.driver.api._

  def findAll(): DBIOAction[List[TeslaOrder], NoStream, Read] =
    orders.sortBy(_.name).result.map(_.toList)

  def findByName(name: String): DBIOAction[Option[TeslaOrder], NoStream, Read] =
    orders.filter(_.name === name).result.headOption

  def updateNew(teslaOrder: TeslaOrder): DBIOAction[Unit, NoStream, Write] = (orders += teslaOrder).map(_ => ())

  def updateEquipment(orderId: Long, equipment: String): DBIOAction[Unit, NoStream, Write] = {
    orders.filter(_.id === orderId).map(_.equipment).update(Some(equipment)).map(_ => ())
  }

  private val orders = TableQuery[Orders]

  private class Orders(tag: Tag) extends Table[TeslaOrder](tag, "orders") {
    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")
    def equipment = column[Option[String]]("equipment")

    def * = (id, name, equipment) <> ((TeslaOrder.apply _).tupled, TeslaOrder.unapply)
  }
}

case class TeslaOrder(id: Long, name: String, equipment: Option[String])