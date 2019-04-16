package tokenring;

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorSelection
import akka.actor.Props

case class Start(nextPath: String, number: Int)
case object MESSAGEONE
case object MESSAGETWO

class TokenRing extends Actor {
  var actor:Int = 0;
  var counterOne:Int = 0;
  var counterTwo:Int = 0;
  var nextActorPath = "";

  def incrementCounterOne = { counterOne += 1; counterOne }
  def incrementCounterTwo = { counterTwo += 1; counterTwo }

  def receive = {
    case MESSAGEONE =>
      this.incrementCounterOne
      Thread.sleep(2000)
      println(" Actor " + actor + " received " + MESSAGEONE.toString() + " number of times : " + counterOne)
      val next = context.actorSelection(nextActorPath)
      next ! MESSAGEONE
    case MESSAGETWO =>
      this.incrementCounterTwo
      Thread.sleep(2000)
      println(" Actor " + actor + " received " + MESSAGETWO.toString() + " number of times : " + counterTwo)
      val next = context.actorSelection(nextActorPath)
      next ! MESSAGETWO
    case Start(nextPath, number) =>
      Thread.sleep(2000)
      actor = number
      nextActorPath = nextPath
  }
}

object Server extends App {
  val system = ActorSystem("TokenRing")

  val first = system.actorOf(Props[TokenRing], name = "first")
  println(first.path)

  val second = system.actorOf(Props[TokenRing], name = "second")
  println(second.path)

  val third = system.actorOf(Props[TokenRing], name = "third")
  println(third.path)

  first ! Start(second.path.toString, 1)
  second ! Start(third.path.toString, 2)
  third ! Start(first.path.toString, 3)

  first ! Start(third.path.toString, 1)
  third ! Start(second.path.toString, 3)
  second ! Start(first.path.toString, 2)

  second ! MESSAGEONE
  third  !  MESSAGETWO

  println("Server Ready")
}

