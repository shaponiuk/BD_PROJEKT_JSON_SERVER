package Queries

import Queries.CheckArgument.Companion.checkArgument
import Queries.Constants.Companion.ERROR_STRING
import Queries.SQLConnection.Companion.con
import java.sql.ResultSet

class ShowPatients {
  companion object {
    private val byPeselKey = "by_pesel"

    val path = "/show_patients"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.containsKey(byPeselKey)) {
          if (checkArgument(it, byPeselKey)) {
            showPatientByPesel(it)
          } else {
            ERROR_STRING
          }
        } else {
          showAllPatients()
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

        rStr += "{\"PESEL\" : ${rs.getInt(1)}, " +
            "\"imie\" : ${rs.getString(2)}, " +
            "\"nazwisko\" : ${rs.getString(3)}}"
      }

      rStr += "]"

      return rStr
    }

    private fun showPatientByPesel(map: Map<String, List<String?>>): String {
      val pesel = map[byPeselKey]!![0]!!

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT *
          FROM pacjenci
          WHERE pesel = $pesel
        """.trimIndent()
      )

      return printRs(rs)
    }

    private fun showAllPatients(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT * FROM pacjenci
        """.trimIndent()
      )

      return printRs(rs)
    }
  }
}