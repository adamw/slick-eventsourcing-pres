package com.softwaremill.example

import scala.concurrent.Future

object EmailService {
  def sendEmail(body: String): Future[Unit] = {
    println("Sending email: " + body)
    Future.successful(())
  }
}
