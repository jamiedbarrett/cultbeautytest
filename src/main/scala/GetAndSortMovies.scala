
/**
  * Created by jamie on 18/02/16.
  */
import play.api.libs.json._
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import System.out.println
import scala.util.Try

case class SearchResult(Search: Seq[MovieInformation], totalResults: String)
case class MovieInformation(Title : String, Year: String, Poster: String)

object GetAndSortMovies {

  implicit val movieReads = Json.format[MovieInformation]
  implicit val searchReads = Json.format[SearchResult]

  val wsClient = NingWSClient()
  val RequestUrl = "http://www.omdbapi.com"
  val ResultType = "movie"
  val NoPoster = "N/A"

  def main(args: Array[String]) = {
    Try(args(0))
      .toOption
      .fold(
        println(s"Missing parameter: movieName")
      )(
        movieName => {

          val movieInfo = request(movieName, 1).flatMap { initialResult =>
            val pages = Seq.range(2, initialResult.totalResults.toInt / 10)
            Future.sequence(pages.map(request(movieName, _))
              ++ Seq(Future.successful(initialResult)))
          }

          val result = Await.result(movieInfo, 20.second)
            .flatMap(search => {
              wsClient.close
              search.Search
            }).filterNot(_.Poster == NoPoster)

          result
            .sortWith(_.Year.toInt < _.Year.toInt)
            .foreach(movie => println(s"${movie.Title} [${movie.Year}] - ${movie.Poster}"))

          println(s"=> ${result.size} result(s) found")
        }
      )
    wsClient.close
  }

  def request(movieName: String, page: Int): Future[SearchResult] = {

    wsClient
      .url(RequestUrl)
      .withQueryString("s" -> movieName, "type" -> ResultType, "page" -> page.toString)
      .withHeaders("Cache-Control" -> "no-cache")
      .get()
      .map (_.json.as[SearchResult])
  }
}
