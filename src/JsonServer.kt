import Queries.*
import com.sun.net.httpserver.HttpServer
import java.io.UnsupportedEncodingException
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

private val PORT = 8001
private val BACKLOG = 1

private val HEADER_ALLOW = "Allow"
private val HEADER_CONTENT_TYPE = "Content-Type"

private val CHARSET = StandardCharsets.UTF_8

private val STATUS_OK = 200
private val STATUS_METHOD_NOT_ALLOWED = 405

private val NO_RESPONSE_LENGTH = -1

private val METHOD_GET = "GET"
private val METHOD_OPTIONS = "OPTIONS"
private val ALLOWED_METHODS = "$METHOD_GET,$METHOD_OPTIONS"

private lateinit var server: HttpServer

private fun dbConnect() {
  Class.forName("oracle.jdbc.driver.OracleDriver")
  SQLConnection.con = DriverManager.getConnection(
    "jdbc:oracle:thin:@labora.mimuw.edu.pl:1521:LABS",
    "sh394322",
    "Klocked0011"
  )
}

private fun contextCreate(path: String, lambda: (Map<String, List<String?>>) -> String) {
  server.createContext(path) { he ->
    try {
      val headers = he.responseHeaders
      val requestMethod = he.requestMethod.toUpperCase()
      when (requestMethod) {
        METHOD_GET -> {
          val requestParameters = getRequestParameters(he.requestURI)
          val responseBody = lambda(requestParameters)
          headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET))
          val rawResponseBody = responseBody.toByteArray(CHARSET)
          he.sendResponseHeaders(STATUS_OK, rawResponseBody.size.toLong())
          he.responseBody.write(rawResponseBody)
        }
        METHOD_OPTIONS -> {
          headers.set(HEADER_ALLOW, ALLOWED_METHODS)
          he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH.toLong())
        }
        else -> {
          headers.set(HEADER_ALLOW, ALLOWED_METHODS)
          he.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH.toLong())
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    } finally {
      he.close()
    }
  }
}

private fun showStockroomContextCreate() {
  contextCreate(ShowStockroom.path, ShowStockroom.lambda)
}

private fun addToStockContextCreate() {
  contextCreate(AddToStockroom.path, AddToStockroom.lambda)
}

private fun largestStockroomIdContextCreate() {
  contextCreate(LargestStockroomId.path, LargestStockroomId.lambda)
}

private fun updateStockContextCreate() {
  contextCreate(UpdateStock.path, UpdateStock.lambda)
}

private fun addVisitContextCreate() {
  contextCreate(AddVisit.path, AddVisit.lambda)
}

private fun showVisitsContextCreate() {
  contextCreate(ShowVisits.path, ShowVisits.lambda)
}

private fun createContexts() {
  showStockroomContextCreate()
  addToStockContextCreate()
  largestStockroomIdContextCreate()
  updateStockContextCreate()
  addVisitContextCreate()
  showVisitsContextCreate()
}

private fun getRequestParameters(requestUri: URI): LinkedHashMap<String, ArrayList<String?>> {
  val requestParameters = LinkedHashMap<String, ArrayList<String?>>()
  val requestQuery = requestUri.rawQuery
  if (requestQuery != null) {
    val rawRequestParameters = requestQuery.split("[&;]".toRegex()).toTypedArray()
    for (rawRequestParameter in rawRequestParameters) {
      val requestParameter = rawRequestParameter.split("=".toRegex(), 2).toTypedArray()
      val requestParameterName = decodeUrlComponent(requestParameter[0])
      requestParameters.putIfAbsent(
        requestParameterName,
        ArrayList()
      )
      val requestParameterValue =
        if (requestParameter.size > 1) decodeUrlComponent(requestParameter[1]) else null

      requestParameters[requestParameterName]!!.add(requestParameterValue)
    }
  }
  return requestParameters
}

private fun decodeUrlComponent(urlComponent: String): String {
  try {
    return URLDecoder.decode(urlComponent, CHARSET.name())
  } catch (ex: UnsupportedEncodingException) {
    throw InternalError(ex)
  }
}

fun main(args: Array<String>) {
  val adress = InetSocketAddress(/*"http://students.mimuw.edu.pl/, */PORT)
  server = HttpServer.create(adress, BACKLOG)

  dbConnect()

  createContexts()

  server.start()
}

