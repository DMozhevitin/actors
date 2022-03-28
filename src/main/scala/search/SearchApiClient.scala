package search

import search.types._
import sttp.client3._
import spray.json._

object SearchApiClient {
  def getTopNResults(searchSystem: SearchSystem, query: String, n: Int): List[SearchResult] = {
    import DefaultJsonProtocol._

    val path = searchSystem.path
    val requestUri = uri"http://localhost:8000/$path?query=$query&count=$n"
    val request = basicRequest.get(requestUri)
    val response = request.send(HttpURLConnectionBackend())

    response.body.flatMap { body =>
      val contents = body
        .parseJson
        .convertTo[List[String]]
      val results = contents.map { SearchResult(_, searchSystem.name) }

      Right(results)
    }.getOrElse(List())
  }
}

