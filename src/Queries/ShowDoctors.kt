package Queries

import Queries.CheckArgument.Companion.checkArgument
import Queries.CheckArgument.Companion.checkInteger
import Queries.Constants.Companion.ERROR_STRING
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
          if (checkArgument(it, withSpecialisationKey)
            && checkInteger(it, withSpecialisationKey)) {
            if (it.containsKey(byIdKey)) {
              if (checkArgument(it, byIdKey)
              && checkInteger(it, byIdKey)) {
                showDoctorsWithSpecialisationById(it)
              } else {
                ERROR_STRING
              }
            } else {
              showDoctorsWithSpecialisation(it)
            }
          } else {
            ERROR_STRING
          }
        } else if (it.containsKey(byIdKey)) {
          if (checkArgument(it, byIdKey)
            && checkInteger(it, byIdKey)) {
            showDoctorsById(it)
          } else {
            ERROR_STRING
          }
        } else {
          showAllDoctors()
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
            "\"specjalizacje_id\" : ${rs.getInt(4)}}"
      }

      rStr += "]"

      return rStr
    }

    private fun showDoctorsWithSpecialisationById(map: Map<String, List<String?>>): String {
      val specialisationId = map[withSpecialisationKey]!![0]!!
      val doctorId = map[byIdKey]!![0]!!

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT *
          FROM lekarze
          WHERE specjalizacje_id = $specialisationId
          AND id = $doctorId
        """.trimIndent()
      )

      return printRs(rs)
    }

    private fun showDoctorsWithSpecialisation(map: Map<String, List<String?>>): String {
      val specialisationId = map[withSpecialisationKey]!![0]!!

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT *
          FROM lekarze
          WHERE specjalizacje_id = $specialisationId
        """.trimIndent()
      )

      return printRs(rs)
    }

    private fun showDoctorsById(map: Map<String, List<String?>>): String {
      val doctorId = map[byIdKey]!![0]!!

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT *
          FROM lekarze
          WHERE id = $doctorId
        """.trimIndent()
      )

      return printRs(rs)
    }

    private fun showAllDoctors(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
        SELECT * FROM lekarze
      """.trimIndent()
      )

      return printRs(rs)
    }
  }
}