package Queries

import Queries.CheckArgument.Companion.checkArgument
import Queries.CheckArgument.Companion.checkInteger
import Queries.Constants.Companion.ERROR_STRING
import Queries.SQLConnection.Companion.con
import java.sql.ResultSet

class ShowServices {
  companion object {
    private val byIdKey = "by_id"

    val path = "/show_services"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.containsKey(byIdKey)) {
          if (checkArgument(it, byIdKey)
            && checkInteger(it, byIdKey)
          ) {
            showServiceById(it)
          } else {
            ERROR_STRING
          }
        } else {
          showAllServices()
        }
      }

    private fun printServiceStockNeed(serviceId: Long): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT zasoby_id, ilosc
          FROM szablon_zasobow_uslug
          WHERE szablon_uslug_id = $serviceId
        """.trimIndent()
      )

      var rStr = "["
      var first = true

      while (rs.next()) {
        if (!first) {
          rStr += ", "
        } else {
          first = false
        }

        rStr += "{\"zasoby_id\" : ${rs.getLong(1)}, " +
            "\"ilosc\" : ${rs.getLong(2)}}"
      }

      rStr += "]"

      return rStr
    }

    private fun printMainRs(rs: ResultSet): String {
      var rStr = "["
      var first = true

      while (rs.next()) {
        if (!first) {
          rStr += ", "
        } else {
          first = false
        }

        rStr += "{\"id\" : ${rs.getLong(1)}, " +
            "\"specjalizacje_id\" : ${rs.getLong(2)}, " +
            "\"nazwa\" : \"${rs.getString(3)}\", " +
            "\"wymagane_zasoby\" : " +
            printServiceStockNeed(rs.getLong(1)) +
            "}"
      }

      rStr += "]"

      return rStr
    }

    private fun showServiceById(map: Map<String, List<String?>>): String {
      val id = map[byIdKey]!![0]!!

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT *
          FROM szablon_uslug
          WHERE id = $id
        """.trimIndent()
      )

      return printMainRs(rs)
    }

    private fun showAllServices(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT * FROM szablon_uslug
        """.trimIndent()
      )

      return printMainRs(rs)
    }
  }
}