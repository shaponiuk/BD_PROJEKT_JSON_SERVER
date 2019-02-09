package Queries

import Queries.CheckArgument.Companion.checkArgument
import Queries.CheckArgument.Companion.checkInteger
import Queries.Constants.Companion.ERROR_STRING
import Queries.SQLConnection.Companion.con
import java.sql.ResultSet

class ShowSpecialisations {
  companion object {
    private val byIdKey = "by_id"

    val path = "/show_specialisations"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.containsKey(byIdKey)) {
          if (checkArgument(it, byIdKey)
            && checkInteger(it, byIdKey)
          ) {
            showSpecialisationById(it)
          } else {
            ERROR_STRING
          }
        } else {
          showAllSpecialisations()
        }
      }

    private fun printRs(rs: ResultSet): String {
      var rStr = "["
      var first = true

      while (rs.next()) {
        if (!first) {
          rStr += ", "
        } else {
          first = false
        }

        rStr += "{\"id\" : ${rs.getLong(1)}, " +
            "\"nazwa\" : \"${rs.getString(2)}\"}"
      }

      rStr += "]"

      return rStr
    }

    private fun showSpecialisationById(map: Map<String, List<String?>>): String {
      val id = map[byIdKey]!![0]!!

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT *
          FROM specjalizacje
          WHERE id = $id
        """.trimIndent()
      )

      return printRs(rs)
    }

    private fun showAllSpecialisations(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT * FROM specjalizacje
        """.trimIndent()
      )

      return printRs(rs)
    }
  }
}