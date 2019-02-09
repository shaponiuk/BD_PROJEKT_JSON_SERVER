package Queries

import Queries.CheckArgument.Companion.checkInteger
import Queries.Constants.Companion.SUCCESS_STRING
import Queries.SQLConnection.Companion.con

// Updating the status of a visit
class UpdateVisit {
  companion object {
    private val visitIdKey = "visit_id"
    private val statusIdKey = "status_id"

    val path = "/update_visit"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (checkArguments(it)) {
          update(it)
        } else {
          "INVALID_ARGUMENTS"
        }
      }

    private fun checkIfVisitExists(visitId: String): Boolean {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT count(*)
          FROM uslugi
          WHERE id = $visitId
        """.trimIndent()
      )

      return if (rs.next()) {
        rs.getInt(1) > 0
      } else {
        false
      }
    }

    private fun checkIfStatusExists(statusId: String): Boolean {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT count(*)
          FROM statusy_uslug
          WHERE id = $statusId
        """.trimIndent()
      )

      return if (rs.next()) {
        rs.getInt(1) > 0
      } else {
        false
      }
    }

    private fun checkArguments(map: Map<String, List<String?>>): Boolean {
      return if (map.containsKey(visitIdKey)
        && map.containsKey(statusIdKey)
      ) {

        val visitIdList = map[visitIdKey]!!
        val statusIdList = map[statusIdKey]!!

        if (!visitIdList.isEmpty()
          && !statusIdList.isEmpty()
        ) {
          val visitId = visitIdList[0]
          val statusId = statusIdList[0]

          if (visitId != null && statusId != null) {
            if (checkInteger(map, visitIdKey)
              && checkInteger(map, statusIdKey)) {
              checkIfVisitExists(visitId)
                  && checkIfStatusExists(statusId)
            } else {
              false
            }
          } else {
            false
          }
        } else {
          false
        }
      } else {
        false
      }
    }

    private fun update(map: Map<String, List<String?>>): String {
      val visitId = map[visitIdKey]!![0]!!
      val statusId = map[statusIdKey]!![0]!!

      val stmt = con.createStatement()
      stmt.executeQuery(
        """
        UPDATE uslugi
        SET status_id = $statusId
        WHERE id = $visitId
        """.trimIndent()
      )

      return SUCCESS_STRING
    }
  }
}