package Queries

import Queries.SQLConnection.Companion.con
import java.sql.ResultSet

class ShowDoctors {
  companion object {
    private val withSpecialisationKey = "with_specialisation"
    private val byIdKey = "by_id"

    val path = "/show_doctors"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.containsKey(withSpecialisationKey)) {
          if (it.containsKey(byIdKey)) {
            TODO()
          } else {
  TODO()
          }
        } else if (it.containsKey(byIdKey)) {
  TODO()
        } else {
          showAll()
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

        rStr += "{\"id\" : ${rs.getInt(1)}, " +
            "\"imie\" : \"${rs.getString(2)}\", " +
            "\"nazwisko\" : \"${rs.getString(3)}\", " +
            "\"specjalizacje_id\" : \"${rs.getString(4)}\"}"
      }

      rStr += "]"

      return rStr
    }

    private fun showAll(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery("""
        SELECT * FROM lekarze
      """.trimIndent())

      return printRs(rs)
    }
  }
}