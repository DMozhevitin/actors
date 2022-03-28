import actors.MasterActor
import actors.types.SearchRequestMessageWithResult
import akka.actor.{ActorSystem, Props}
import org.scalatest._
import org.scalatest.funsuite.AnyFunSuite
import com.xebialabs.restito.server.StubServer

import scala.collection.mutable
import search.types._

class StubServerTest extends AnyFunSuite with BeforeAndAfter {
  private var server: StubServer = _
  private val allSearchSystems = List(
    Google("google"),
    Yandex("yandex"),
    Bing("bing")
  )

  def baseTest(testName: String,
               searchSystems: List[(SearchSystem, Option[Int])],
               count: Int,
               query: String,
               timeout: Int,
               expected: List[SearchResult]): Unit = {
    test(testName) {
      searchSystems.foreach { case (ss, tl) =>
        utils.prepareResponse(server, ss.path, tl)
      }

      val system = ActorSystem("TestSystem")
      val props = Props(classOf[MasterActor], allSearchSystems)
      val masterActor = system.actorOf(props, "master")

      val msg = SearchRequestMessageWithResult(query, count, timeout, mutable.Buffer())
      masterActor ! msg

      Thread.sleep(timeout)

      val actual = msg.result
      assert(expected.size == actual.size && expected.toSet == actual.toSet)
    }
  }

  def mkExpected(searchSystems: List[SearchSystem], query: String, count: Int): List[SearchResult] =
    searchSystems.flatMap { ss =>
      (1 to count).map { i =>
        val res = s"$i:$query"
        SearchResult(res, ss.name)
      }
    }

  before {
    server = new StubServer(8000)
    server.start()
  }

  after {
    server.stop()
  }

  baseTest(
    testName = "sample test",
    searchSystems = allSearchSystems.map { (_, None)},
    count = 5,
    query = "query",
    timeout = 3000,
    expected = mkExpected(allSearchSystems, "query", 5)
  )

  baseTest(
    testName = "no search systems",
    searchSystems = List(),
    count = 100,
    query = "query",
    timeout = 3000,
    expected = List()
  )

  baseTest(
    testName = "all requests exceed time out",
    searchSystems = allSearchSystems.map { (_, Some(3000)) },
    count = 100,
    query = "query",
    timeout = 2000,
    expected = List()
  )

  baseTest(
    testName = "bing exceeds time out",
    searchSystems = allSearchSystems.map {
      case ss@Bing(_) => (ss, Some(10_000))
      case ss => (ss, None)
    },
    count = 5,
    query = "query",
    timeout = 3000,
    expected = mkExpected(
      allSearchSystems.filter {
        case Bing(_) => false
        case _ => true
      }, "query", 5
    )
  )

}
