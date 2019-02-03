package Queries

import Queries.SQLConnection.Companion.con

class LargestStockroomId {
  companion object {
    val path = "largest_stockroom_id"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        val stmt = con.createStatement()
        val rs = stmt.executeQuery(
          """
          SELECT max(id) FROM zasoby
        """.trimIndent()
        )

        var max_id = -1
        if (rs.next())
          max_id = rs.getInt(1)

        "{\"max_id\" : ${max_id}}"
      }
  }
}