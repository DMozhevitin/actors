package actors

import actors.types.{SearchRequestMessage, SearchResponseMessage}
import akka.actor.Actor
import search.SearchApiClient
import search.types.SearchSystem

case class ChildActor(searchSystem: SearchSystem) extends Actor {
  def receive: Receive = {
    case SearchRequestMessage(query, count) =>
      val res = SearchApiClient.getTopNResults(searchSystem, query, count)
      sender.tell(SearchResponseMessage(res), self)
      context.stop(self)
  }
}
