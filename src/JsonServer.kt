import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

object JsonServer {
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

  private val ERROR_STRING = "ERROR"

  private val TRUTH_VALUE = "TRUE"
  private val FALSE_VALUE = "FALSE"

  private val showMagPath = "/show_mag"
  private val addToMagPath = "/add_to_mag"

  private val showMagShowZeroKey = "show_zero"

  // TODO:
  private fun showMagAll(): String = "TODO: showMagAll()"

  // TODO:
  private fun showMagZeroCap(): String = "TODO: showMagZeroCap()"

  private val showMagLambda: (Map<String, List<String?>>) -> String =
    {
      if (it.isEmpty()) {
        showMagAll()
      } else {
        if (it.containsKey(showMagShowZeroKey)) {
          val bVal = it[showMagShowZeroKey]

          if (bVal == null
            || bVal.isEmpty()
          ) {
            ERROR_STRING
          } else {
            when (bVal[0]) {
              TRUTH_VALUE -> showMagZeroCap()
              FALSE_VALUE -> ERROR_STRING
              else -> ERROR_STRING
            }
          }
        } else {
          showMagAll()
        }
      }
    }

  private val addToMagLambda: (Map<String, List<String?>>) -> String =
    {
      "TODO"
    }

  private lateinit var server: HttpServer

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

  private fun createShowMagContext() {
    contextCreate(showMagPath, showMagLambda)
  }

  fun createAddToMagContext() {
    contextCreate(addToMagPath, addToMagLambda)
  }

  @Throws(IOException::class)
  @JvmStatic
  fun main(args: Array<String>) {
    val adress = InetSocketAddress(/*"http://students.mimuw.edu.pl/, */PORT)
    server = HttpServer.create(adress, BACKLOG)

    createShowMagContext()
    createAddToMagContext()

    server.start()
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
}
