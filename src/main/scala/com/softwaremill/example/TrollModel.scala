package com.softwaremill.example

import com.softwaremill.events.EventsDatabase
import slick.dbio.Effect.{Write, Read}

import scala.concurrent.ExecutionContext

class TrollModel(protected val database: EventsDatabase)(implicit val ec: ExecutionContext) {

  import database.driver.api._

  def findAll(): DBIOAction[List[Troll], NoStream, Read] =
    trolls.sortBy(_.name).result.map(_.toList)

  def findByName(name: String): DBIOAction[Option[Troll], NoStream, Read] =
    trolls.filter(_.name === name).result.headOption

  def updateNew(troll: Troll): DBIOAction[Unit, NoStream, Write] = (trolls += troll).map(_ => ())

  def updateEquipment(trollId: Long, equipment: String): DBIOAction[Unit, NoStream, Write] = {
    trolls.filter(_.id === trollId).map(_.equipment).update(Some(equipment)).map(_ => ())
  }

  private val trolls = TableQuery[Trolls]

  private class Trolls(tag: Tag) extends Table[Troll](tag, "trolls") {
    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")
    def equipment = column[Option[String]]("equipment")

    def * = (id, name, equipment) <> ((Troll.apply _).tupled, Troll.unapply)
  }
}

case class Troll(id: Long, name: String, equipment: Option[String])