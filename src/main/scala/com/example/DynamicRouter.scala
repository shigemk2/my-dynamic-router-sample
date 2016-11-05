package com.example

import akka.actor.Actor.Receive
import akka.actor._

case class InterestedIn(messageType: String)
case class NoLongerInterestedIn(messageType: String)

case class TypeAMessage(description: String)
case class TypeBMessage(description: String)
case class TypeCMessage(description: String)
case class TypeDMessage(description: String)

object DynamicRouterDriver extends CompletableApp(5) {
}

class DunnoInterested extends Actor {
  override def receive: Receive = {
    case message: Any =>
      println(s"DuunoInterest: received undeliverable message: $message")
      DynamicRouterDriver.completedStep()
  }
}

class TypeAInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeAMessage.getClass.getName)

  override def receive: Receive = {
    case message: TypeAMessage =>
      println(s"TypeAInterested: received: $message")
      DynamicRouterDriver.completedStep()
    case message: Any =>
      println(s"TypeAInterested: received unexpected message: $message")
  }
}

class TypeBInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeBMessage.getClass.getName)

  override def receive: Receive = {
    case message: TypeBMessage =>
      println(s"TypeBInterested: received: $message")
      DynamicRouterDriver.completedStep()
    case message: Any =>
      println(s"TypeBInterested: received unexpected message: $message")
  }
}

class TypeCInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeCMessage.getClass.getName)

  override def receive: Receive = {
    case message: TypeCMessage =>
      println(s"TypeCInterested: received: $message")

      interestRouter ! NoLongerInterestedIn(TypeCMessage.getClass.getName)

      DynamicRouterDriver.completedStep()

    case message: Any =>
      println(s"TypeCInterested: received unexpected message: $message")
  }
}

class TypeCAlsoInterested(interestRouter: ActorRef) extends Actor {
  interestRouter ! InterestedIn(TypeCMessage.getClass.getName)

  override def receive: Receive = {
    case message: TypeCMessage =>
      println(s"TypeCAlsoInterested: received: $message")

      interestRouter ! NoLongerInterestedIn(TypeCMessage.getClass.getName)

      DynamicRouterDriver.completedStep()

    case message: Any =>
      println(s"TypeCAlsoInterested: received unexpected message: $message")
  }
}