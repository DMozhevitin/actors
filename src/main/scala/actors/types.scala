package actors

import akka.actor.ActorRef
import search.types.SearchResult

import scala.collection.mutable

object types {
  case class SearchRequestMessage(query: String, count: Int)

  case class SearchRequestMessageWithResult(query: String,
                                            count: Int,
                                            timeout: Int,
                                            result: mutable.Buffer[SearchResult])

  case class SearchResponseMessage(response: List[SearchResult])

  case class MasterActorState(results: mutable.Buffer[SearchResult],
                              completed: Set[ActorRef],
                              requester: Option[ActorRef])
}
