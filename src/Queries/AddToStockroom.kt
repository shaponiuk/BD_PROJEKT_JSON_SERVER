package Queries

import Queries.Constants.Companion.ERROR_STRING
import Queries.Constants.Companion.SUCCESS_STRING
import Queries.SQLConnection.Companion.con
import java.lang.NumberFormatException

class AddToStockroom {
  companion object {
    private val nameKey = "name"
    private val ammountKey = "ammount"

    val path = "/add_to_stock"

    val lambda: (Map<String, List<String?>>) -> String =
      {
        if (it.size > 2) {
          "TOO_MANY_ARGUMENTS"
        } else {
          if (it.containsKey(nameKey)
            && it.containsKey(ammountKey)
          ) {
            val nameList = it[nameKey]
            val ammountList = it[ammountKey]

            if (nameList != null && ammountList != null) {
              if (nameList.size > 0 && ammountList.size > 0) {
                if (nameList[0] != null && ammountList[0] != null) {
                  addToStock(nameList[0]!!, ammountList[0]!!)
                } else {
                  "NULL FIRST ARGUMENT IN ONE LIST"
                }
              } else {
                "EMPTY ARGUMENT LISTS"
              }
            } else {
              "NULL_VALUE"
            }
          } else {
            "INVALID_ARGUMENT_KEYS"
          }
        }
      }


    private fun checkName(name: String): Boolean {
      if (name.contains('\'')
      || name.contains(';')) {
        return false
      } else {
        return name.isNotEmpty()
      }
    }

    private fun checkAmmount(ammount: String): Boolean {
      var isInt = false

      try {
        Integer.parseInt(ammount)
        isInt = true
      } catch (e: NumberFormatException) {
      }

      return isInt
    }

    private fun addToStock(name: String, ammount: String): String {
      if (checkName(name) && checkAmmount(ammount)) {
        val newId = Integer.parseInt(LargestStockroomId.lambda(HashMap())) + 1

        val stmt = con.createStatement()
        stmt.executeQuery("""
          INSERT INTO zasoby VALUES
          ($newId, $name, $ammount)
        """.trimIndent())

        return SUCCESS_STRING
      } else {
        return ERROR_STRING
      }
    }
  }
}