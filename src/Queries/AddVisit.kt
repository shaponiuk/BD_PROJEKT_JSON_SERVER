package Queries

import Queries.Constants.Companion.ERROR_STRING
import Queries.SQLConnection.Companion.con
import java.lang.NumberFormatException
import java.util.*

class AddVisit {
  companion object {
    private val serviceIdKey = "service_id"
    private val peselKey = "pesel"
    private val doctorIdKey = "doctor_id"
    private val dateKey = "date"

    val path = "/add_visit"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.size < 3) {
          "TOO_FEW_ARGUMENTS"
        } else {
          if (!it.containsKey(serviceIdKey)
            || !it.containsKey(peselKey)
            || !it.containsKey(doctorIdKey)
            || !it.containsKey(dateKey)
          ) {
            "WRONG_ARGUMENT_KEYS"
          } else {
            if (it[serviceIdKey] == null
              || it[peselKey] == null
              || it[doctorIdKey] == null
              || it[dateKey] == null
            ) {
              "NULL_ARGUMENT_VALUE"
            } else if (it[serviceIdKey]!!.isEmpty()
              || it[peselKey]!!.isEmpty()
              || it[doctorIdKey]!!.isEmpty()
              || it[dateKey]!!.isEmpty()
            ) {
              "EMPTY_ARGUMENT_LIST"
            } else {
              val argList = LinkedList<String?>().apply {
                add(it[serviceIdKey]!![0])
                add(it[peselKey]!![0])
                add(it[doctorIdKey]!![0])
                add(it[dateKey]!![0])
              }

              addVisit(argList)
            }
          }
        }
      }

    private fun checkNotNull(argList: List<String?>): Boolean {
      for (arg in argList) {
        if (arg == null) {
          return false
        }
      }

      return true
    }

    private fun checkServiceId(id: String): Boolean {
      try {
        Integer.parseInt(id)
      } catch (e: NumberFormatException) {
        return false
      }

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
        SELECT count(*) FROM szablon_uslug WHERE id = $id
      """.trimIndent()
      )

      var count = 0

      if (rs.next()) {
        count = rs.getInt(1)
      }

      return count == 0
    }

    private fun checkPesel(pesel: String): Boolean {
      try {
        Integer.parseInt(pesel)
      } catch (e: NumberFormatException) {
        return false
      }

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
        SELECT count(*) FROM pacjenci WHERE pesel = $pesel
      """.trimIndent()
      )

      var count = 0

      if (rs.next()) {
        count = rs.getInt(1)
      }

      return count == 0
    }

    // Assumes Strings are not null
    private fun otherChecks(argList: List<String?>): Boolean {
      return checkServiceId(argList[0]!!)
      && checkPesel(argList[1]!!)
    }

    private fun checkArguments(argList: List<String?>): Boolean {
      if (!checkNotNull(argList))
        return false

      return otherChecks(argList)
    }

    private fun addVisit(argList: List<String?>): String {
      if (checkArguments(argList)) {

      }

      return ERROR_STRING
    }
  }
}