package Queries

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
  }
}