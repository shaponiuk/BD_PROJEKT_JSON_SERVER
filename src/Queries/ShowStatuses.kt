package Queries

import Queries.SQLConnection.Companion.con
import java.sql.ResultSet

class ShowStatuses {
  companion object {
    val path = "/show_statuses"

    val lambda: (Map<String, List<String?>>) -> String = {
      showAllStatuses()
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

    private fun showAllStatuses(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT * FROM statusy_uslug
        """.trimIndent()
      )

      return printRs(rs)
    }
  }
}