package Queries

import Queries.Constants.Companion.ERROR_STRING
import Queries.Constants.Companion.SUCCESS_STRING
import Queries.SQLConnection.Companion.con
import java.lang.NumberFormatException

class UpdateStock {
  companion object {
    private val nameKey = "name"
    private val ammountKey = "ammount"

    val path = "/update_stock"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.size < 2) {
          "TOO_FEW_ARGUMENTS"
        } else if (!it.containsKey(nameKey)
          || !it.containsKey(ammountKey)
        ) {
          "ARGUMENT(S)_MISSING"
        } else {
          val nameList = it[nameKey]
          val ammountList = it[ammountKey]

          if (nameList == null
            || ammountList == null
          ) {
            "NULL_ARGUMENT_LISTS"
          } else if (nameList.size > 0 && ammountList.size > 0) {
            addToStock(nameList[0], ammountList[0])
          } else {
            "EMPTY_ARGUMENT_LISTS"
          }
        }
      }

    fun checkIfNameExists(name: String): Boolean {
      val stmt = con.createStatement()
      val rs = stmt.executeQuery(
        """
          SELECT count(*) FROM zasoby WHERE nazwa = '$name'
        """.trimIndent()
      )

      return if (rs.next()) {
        rs.getInt(1) > 0
      } else {
        false
      }
    }

    fun checkArguments(name: String?, ammount: String?): Boolean {
      if (name == null || ammount == null) {
        return false
      } else if (name.contains('\'')
        || name.contains(';')
      ) {
        return false
      } else {
        try {
          Integer.parseInt(ammount)
        } catch (e: NumberFormatException) {
          return false
        }

        return checkIfNameExists(name)
      }
    }

    fun addToStock(name: String?, ammount: String?): String {
      if (checkArguments(name, ammount)) {
        val stmt = con.createStatement()
        val rs = stmt.executeQuery(
          """
            UPDATE TABLE zasoby
            SET stan = stan + $ammount
            WHERE nazwa = '$name'
          """.trimIndent()
        )

        if (rs.rowUpdated()) {
          return SUCCESS_STRING
        }
      }

      return ERROR_STRING
    }
  }
}