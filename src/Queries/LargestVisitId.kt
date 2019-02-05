package Queries

import Queries.SQLConnection.Companion.con

class LargestVisitId {
  companion object {
    fun getLargestVisitId(): Int {
      val stmt = con.createStatement()
      val rsc = stmt.executeQuery(
        """
          SELECT count(*) FROM uslugi
        """.trimIndent()
      )

      if (rsc.next()) {
        val count = rsc.getInt(1)
        if (count == 0) {
          return -1
        }
      }

      val stmt2 = con.createStatement()
      val rsi = stmt2.executeQuery(
        """
          SELECT max(id) FROM uslugi
        """.trimIndent()
      )

      if (rsi.next()) {
        return rsi.getInt(1)
      } else {
        return -1
      }
    }
  }
}