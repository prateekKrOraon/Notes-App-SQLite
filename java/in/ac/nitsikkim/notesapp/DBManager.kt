package `in`.ac.nitsikkim.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DBManager {

    val dbName = "myNotes"
    val dbTable = "notes"
    val colID = "id"
    val colTitle = "title"
    val colDes = "description"
    val dbVersion = 1

    //create table sql query
    val createTableQuery = "CREATE TABLE IF NOT EXISTS $dbTable ($colID INTEGER PRIMARY KEY, $colTitle TEXT, $colDes TEXT)"
    var sqldb:SQLiteDatabase?=null

    constructor(context:Context){
        var dbHelper = DatabaseHelper(context)
        sqldb = dbHelper.writableDatabase

    }

    inner class DatabaseHelper:SQLiteOpenHelper{
        var context:Context?=null

        constructor(context: Context):super(context,dbName,null,dbVersion){
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(createTableQuery)
            Toast.makeText(context,"Table created",Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS $dbTable")
        }

    }

    /* function to insert row into database */
    fun insert(values:ContentValues):Long{
        val id = sqldb!!.insert(dbTable,"",values)
        return id
    }

    /* function to retrieve from database */
    fun query(projection:Array<String>,selection:String,selectionArgs:Array<String>,sortOrder:String):Cursor{

        val db = SQLiteQueryBuilder()
        db.tables=dbTable
        val cursor = db.query(sqldb,projection,selection,selectionArgs,null,null,sortOrder)
        return cursor

    }

    /* function to delete row from database */
    fun delete(selection: String,selectionArgs: Array<String>):Int{
        val num = sqldb!!.delete(dbTable,selection,selectionArgs)
        return num
    }

    /* function to update row in database */
    fun update(values:ContentValues,selection: String,selectionArgs: Array<String>):Int{
        val num = sqldb!!.update(dbTable,values,selection,selectionArgs)
        return num
    }

}