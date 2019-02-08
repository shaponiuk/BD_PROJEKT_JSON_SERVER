package Queries

import Queries.Constants.Companion.ERROR_STRING
import Queries.Constants.Companion.SUCCESS_STRING
import Queries.LargestVisitId.Companion.getLargestVisitId
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
          System.out.println("NULL ARG")
          return false
        }
      }

      return true
    }

    private fun checkServiceId(id: String): Boolean {
      try {
        Integer.parseInt(id)
      } catch (e: NumberFormatException) {
        System.out.println("SERVICE_ID_NOT_NUMBER")
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

      if (count == 0)
        System.out.println("no valid service ids in the base")

      return count > 0
    }

    private fun checkPesel(pesel: String): Boolean {
      try {
        pesel.toLong()
      } catch (e: NumberFormatException) {
        System.out.println("pesel not int")
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

      if (count == 0)
        System.out.println("no valid pesels in the base")

      return count > 0
    }

    private fun checkDoctorId(doctorId: String): Boolean {
      try {
        Integer.parseInt(doctorId)
      } catch (e: NumberFormatException) {
        System.out.println("doctorId not int")
        return false
      }

      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT count(*) FROM lekarze WHERE id = '$doctorId'
        """.trimIndent()
      )

      var count = 0

      if (rs.next()) {
        count = rs.getInt(1)
      }

      if (count == 0)
        System.out.println("no valid doctorIds in the base")

      return count > 0
    }

    private fun checkDate(date: String): Boolean {
      if (date.contains('\'')
        || date.contains(';')
        || date.length > 11
      ) {
        System.out.println("date has \\ or ;")
        return false
      } else {
        val stmt = con.createStatement()
        val qs =
          """
            SELECT 1 FROM dual WHERE TO_DATE('${date.subSequence(0, 9)}', 'dd/mm/yyyy  ') > current_date - 1
          """.trimIndent()
        System.out.println(qs)
        val rs = stmt.executeQuery(qs)

        var count = 0

        if (rs.next()) {
          count = rs.getInt(1)
        }

        if (count != 0)
          System.out.println("too early date")

        return count == 1
      }
    }

    private fun checkSpecialisation(serviceId: String, doctorId: String): Boolean {
      val stmtS = con.createStatement()
      val rsS = stmtS.executeQuery(
        """
          SELECT specjalizacje_id FROM szablon_uslug
          WHERE id = $serviceId
        """.trimIndent()
      )

      if (rsS.next()) {
        val stmtD = con.createStatement()
        val rsD = stmtD.executeQuery(
          """
            SELECT specjalizacje_id FROM lekarze
            WHERE id = $doctorId
          """.trimIndent()
        )

        return if (rsD.next()) {
          val specLeft = rsS.getInt(1)
          val specRight = rsD.getInt(1)

          if (specLeft != specRight)
            System.out.println("serviceId does not match doctor's specialisation")
          specLeft == specRight
        } else {
          false
        }
      } else {
        return false
      }
    }

    private fun checkStock(serviceId: String): Boolean {
      val stmt1 = con.createStatement()
      val rs = stmt1.executeQuery(
        """
          SELECT zasoby_id, ilosc
          FROM szablon_zasobow_uslug
          WHERE szablon_uslug_id = $serviceId
        """.trimIndent()
      )

      while (rs.next()) {
        val stockId = rs.getInt(1)
        val ammount = rs.getInt(2)

        val stmt2 = con.createStatement()
        val rs2 = stmt2.executeQuery(
          """
            SELECT stan
            FROM zasoby
            WHERE id = $stockId
          """.trimIndent()
        )

        while (rs2.next()) {
          if (rs2.getInt(1) < ammount) {
            System.out.println("too few elements")
            return false
          }
        }
      }

      return true
    }

    // Assumes Strings are not null
    private fun otherChecks(argList: List<String?>): Boolean {
      return checkServiceId(argList[0]!!)
          && checkPesel(argList[1]!!)
          && checkDoctorId(argList[2]!!)
          && checkDate(argList[3]!!)
          && checkSpecialisation(argList[0]!!, argList[2]!!)
          && checkStock(argList[0]!!)
    }

    private fun checkArguments(argList: List<String?>): Boolean {
      if (!checkNotNull(argList))
        return false

      return otherChecks(argList)
    }

    private fun updateStock(serviceId: String) {
      val stmt1 = con.createStatement()
      val rs = stmt1.executeQuery(
        """
          SELECT zasoby_id, ilosc
          FROM szablon_zasobow_uslug
          WHERE szablon_uslug_id = $serviceId
        """.trimIndent()
      )

      while (rs.next()) {
        val stockId = rs.getInt(1)
        val ammount = rs.getInt(2)

        val stmt2 = con.createStatement()
        stmt2.executeQuery(
          """
            UPDATE zasoby
            SET stan = stan - $ammount
            WHERE id = $stockId
          """.trimIndent()
        )
      }
    }

    private fun addVisit(argList: List<String?>): String {
      if (checkArguments(argList)) {
        val id = getLargestVisitId() + 1
        val visitStatusId = 0

        val stmt = con.createStatement()
        stmt.executeQuery(
          """
            INSERT INTO uslugi
            VALUES ($id, $visitStatusId, ${argList[1]},
            ${argList[2]}, '${argList[3]}', ${argList[0]})
          """.trimIndent()
        )

        updateStock(argList[0]!!)

        return SUCCESS_STRING
      }

      return ERROR_STRING
    }
  }
}