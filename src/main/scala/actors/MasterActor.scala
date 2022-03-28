package actors

import actors.types.{SearchRequestMessage, SearchRequestMessageWithResult, SearchResponseMessage}
import akka.actor.{Actor, ActorRef, Props}
import search.types.{SearchResult, SearchSystem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.Future

class MasterActor(searchSystems: List[SearchSystem]) extends Actor {

  private var results: mutable.Buffer[SearchResult] = _
  private val completed = mutable.Set[ActorRef]()
  private var requester: ActorRef = _

  def receive: Receive = {
    case SearchRequestMessageWithResult(query, count, timeout, result) =>
      requester = sender
      results = result

      searchSystems.zipWithIndex.foreach { case (ss, i) =>
        val child = context.actorOf(Props(classOf[ChildActor], ss), i.toString)
        child.tell(SearchRequestMessage(query, count), self)
      }

      Future {
        Thread.sleep(timeout)
        requester.tell(SearchResponseMessage(results.toList), self)
        context.stop(self)
      }

    case SearchResponseMessage(res) =>
      results ++= res
      completed += sender

      if (completed.size == searchSystems.size) {
        requester.tell(SearchResponseMessage(results.toList), self)
        context.stop(self)
      }
  }

}

