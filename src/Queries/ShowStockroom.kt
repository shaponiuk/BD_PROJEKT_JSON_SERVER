package Queries

import Queries.SQLConnection.Companion.con

class ShowStockroom {
  companion object {
    private val id = "id"
    private val nazwa = "nazwa"
    private val stan = "stan"

    val path = "/show_stock"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.isEmpty()) {
          showStockAll()
        } else {
          if (it.containsKey(showStockShowZeroKey)) {
            val bVal = it[showStockShowZeroKey]

            if (bVal == null
              || bVal.isEmpty()
            ) {
              Constants.ERROR_STRING
            } else {
              when (bVal[0]) {
                Constants.TRUTH_VALUE -> showStockZero()
                Constants.FALSE_VALUE -> Constants.ERROR_STRING
                else -> Constants.ERROR_STRING
              }
            }
          } else {
            showStockAll()
          }
        }
      }

    private val showStockShowZeroKey = "show_zero"

    private fun showStockAll(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery("SELECT * FROM zasoby")

      var testOutput = "["

      var first = true

      while (rs.next()) {
        if (!first)
          testOutput += ", "
        else
          first = false

        testOutput += "{"
        testOutput += "\"" + id + "\" : "
        testOutput += rs.getInt(id).toString() + ", "
        testOutput += "\"" + nazwa + "\" : "
        testOutput += rs.getString(nazwa) + ", "
        testOutput += "\"" + stan + "\" : "
        testOutput += rs.getInt(stan)
        testOutput += "}"
      }

      testOutput += "]"

      return testOutput
    }

    private fun showStockZero(): String = "TODO"
  }
}