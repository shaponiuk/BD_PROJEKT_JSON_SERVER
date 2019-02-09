package Queries

import java.lang.NumberFormatException

class CheckArgument {
  companion object {
    fun checkArgument(map: Map<String, List<String?>>, key: String): Boolean {
      val argList = map[key]

      return if (argList != null) {
        if (!argList.isEmpty()) {
          return argList[0] != null
        } else {
          false
        }
      } else {
        false
      }
    }

    // Assures the arg has passed the checkArgument test
    fun checkInteger(map: Map<String, List<String?>>, key: String): Boolean {
      val i = map[key]!![0]!!

      try {
        i.toLong()
      } catch (e: NumberFormatException) {
        return false
      }

      return true
    }
  }
}