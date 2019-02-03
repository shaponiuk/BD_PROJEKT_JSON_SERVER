package Queries

import Queries.Constants.Companion.ERROR_STRING
import Queries.SQLConnection.Companion.con
import java.lang.NumberFormatException
import java.sql.ResultSet

class ShowStockroom {
  companion object {
    private val id = "id"
    private val nazwa = "nazwa"
    private val stan = "stan"

    private val showStockShowZeroKey = "show_zero"
    private val showStockShowNonZeroKey = "show_nonzero"
    private val findByIdKey = "find_by_id"

    val path = "/show_stock"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.isEmpty()) {
          showStockAll()
        } else {
          if (it.values.size > 1) {
            ERROR_STRING
          } else {
            if (it.containsKey(showStockShowZeroKey)) {
              showStockZero()
            } else if (it.containsKey(showStockShowNonZeroKey)) {
              showStockNonZero()
            } else if (it.containsKey(findByIdKey)) {
              val list = it[findByIdKey]
              if (list != null) {
                if (list.size > 0) {
                  try {
                    list[0]
                    val parsedInt = Integer.parseInt(list[0])
                    showStockById(parsedInt)
                  } catch (e: NumberFormatException) {
                    "NUMBER_FORMAT_EXCEPTION"
                  }
                } else {
                  "EMPTY LIST"
                }
              } else {
                "NULL LIST"
              }
            } else {
              ERROR_STRING
            }
          }
        }
      }

    private fun printRs(rs: ResultSet): String {
      var output = "["
      var first = true

      while (rs.next()) {
        if (!first)
          output += ", "
        else
          first = false

        output += "{\"$id\" : ${rs.getInt(id)}, " +
            "\"$nazwa\" : \"${rs.getString(nazwa)}\", " +
            "\"$stan\" : ${rs.getInt(stan)}}"
      }

      output += "]"

      return output
    }

    private fun showStockAll(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery("SELECT * FROM zasoby")
      return printRs(rs);
    }

    private fun showStockZero(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """SELECT * FROM zasoby
           WHERE stan = 0"""
      )
      return printRs(rs)
    }

    private fun showStockNonZero(): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """SELECT * FROM zasoby WHERE stan > 0"""
      )
      return printRs(rs)
    }

    private fun showStockById(id: Int): String {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """SELECT * FROM zasoby WHERE id = $id"""
      )
      return printRs(rs)
    }
  }
}
