package Queries

import Queries.SQLConnection.Companion.con

class LargestStockroomId {
  companion object {
    val path = "/largest_stockroom_id"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        "{\"max_id\" : ${getLargestId()}}"
      }

    fun getLargestId(): Int {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT max(id) FROM zasoby
        """.trimIndent()
      )

      if (rs.next()) {
        return rs.getInt(1)
      } else {
        return -1
      }
    }
  }
}