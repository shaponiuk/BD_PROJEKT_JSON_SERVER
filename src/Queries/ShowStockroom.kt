package Queries

class ShowStockroom {
  companion object {
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

    private fun showStockAll(): String = "TODO"

    private fun showStockZero(): String = "TODO"
  }
}