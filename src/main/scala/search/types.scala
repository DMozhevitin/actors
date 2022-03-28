package search

object types {
  sealed trait SearchSystem {
    val path: String
    val name: String
  }

  case class Google(override val path: String) extends SearchSystem {
    override val name = "google"
  }

  case class Yandex(override val path: String) extends SearchSystem {
    override val name = "yandex"
  }

  case class Bing(override val path: String) extends SearchSystem {
    override val name = "bing"
  }

  case class SearchResult(contents: String, source: String)
}
