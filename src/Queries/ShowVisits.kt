package Queries

import Queries.SQLConnection.Companion.con
import java.sql.ResultSet

class ShowVisits {
  companion object {
    private val forDoctorKey = "for_doctor"
    private val forPeselKey = "for_pesel"

    val path = "/show_visits"
    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.containsKey(forDoctorKey)) {
          if (it.containsKey(forPeselKey)) {
            TODO()
          } else {
            TODO()
          }
        } else if (it.containsKey(forPeselKey)) {
          TODO()
        } else {
          showAll()
        }
      }

    private fun printRs(rs: ResultSet): String {
      var returnString = "["
      var first = true

      while (rs.next()) {
        if (!first) {
          returnString += ", "
        } else {
          first = false
        }

        returnString += "{\"id\" : ${rs.getInt(1)}, " +
            "\"status_id\" : ${rs.getInt(2)}, " +
            "\"pacjenci_PESEL\" : ${rs.getLong(3)}, " +
            "\"lekarze_id\" : ${rs.getInt(4)}, " +
            "\"data\" : ${rs.getDate(5)}, " +
            "\"szablon_uslug_id\" : ${rs.getInt(6)}}"
      }

      returnString += "]"

      return returnString
    }

    private fun showAll(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT * FROM uslugi
        """.trimIndent()
      )

      return printRs(rs)
    }
  }
}