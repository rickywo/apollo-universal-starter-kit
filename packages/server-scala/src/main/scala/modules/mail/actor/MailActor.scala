package modules.mail.actor

import akka.actor.{Actor, ActorLogging}
import com.github.jurajburian.mailer.{Mailer, Message}
import common.ActorNamed
import modules.common.FieldError
import modules.mail.actor.MailActor.SendMail
import modules.mail.models.MailPayload

import scala.util.{Failure, Success, Try}

object MailActor extends ActorNamed {

  case class SendMail(message: Message, mailer: Mailer)

  final val name = "MailActor"
}

class MailActor extends Actor with ActorLogging {

  def receive: Receive = {
    case sendMail: SendMail =>
      log.info(s"Received message: [ $sendMail ]")
      val payload = Try {
        sendMail.mailer.send(sendMail.message)
      } match {
        case Success(_) => MailPayload()
        case Failure(exception) => MailPayload(Some(List(FieldError("", exception.getMessage))))
      }
      sender ! payload

    case _ => log.warning("Received unknown message")
  }
}