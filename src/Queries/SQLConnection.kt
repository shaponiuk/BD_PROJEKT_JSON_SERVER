package Queries

import java.sql.Connection

class SQLConnection {
  companion object {
    lateinit var con: Connection
  }
}