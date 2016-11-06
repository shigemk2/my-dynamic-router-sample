package com.example

import akka.actor.Actor.Receive
import reflect.runtime.currentMirror
import akka.actor._

case class InterestedIn(messageType: String)
case class NoLongerInterestedIn(messageType: String)

case class TypeAMessage(description: String)
case class TypeBMessage(description: String)
case class TypeCMessage(description: String)
case class TypeDMessage(description: String)

object DynamicRouterDriver extends CompletableApp(5) {
}

class TypedMessageInterestRouter(
                                  dunnoInterested: ActorRef,
                                  canStartAfterRegistered: Int,
                                  canCompleteAfterUnregistered: Int) extends Actor {
  val interestRegistry = scala.collection.mutable.Map[String, ActorRef]()
  val secondaryInterestRegistry = scala.collection.mutable.Map[String, ActorRef]()

  override def receive: Receive = {
    case interestedIn: InterestedIn =>
      registerInterest(interestedIn)
    case noLongerInterestedIn: NoLongerInterestedIn =>
      unregisterInterest(noLongerInterestedIn)
    case message: Any =>
      sendFor(message)
  }

  def registerInterest(interestedIn: InterestedIn) = {
    val messageType = typeOfMessage(interestedIn.messageType)
    if (!interestRegistry.contains(messageType)) {
      interestRegistry(messageType) = sender
    } else {
      secondaryInterestRegistry(messageType) = sender
    }

    if (interestRegistry.size + secondaryInterestRegistry.size >= canStartAfterRegistered) {
      DynamicRouterDriver.canStartNow()
    }
  }

  def sendFor(message: Any) = {
    val messageType = typeOfMessage(currentMirror)
  }


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