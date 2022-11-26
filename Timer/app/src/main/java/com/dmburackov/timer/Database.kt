package com.dmburackov.timer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

const val DATABASE_NAME = "db"
const val TABLE_NAME = "workouts"
const val COL_ID = "id"
const val COL_TITLE = "title"
const val COL_WARMUP = "warmup"
const val COL_WORK = "work"
const val COL_REST = "rest"
const val COL_CYCLES = "cycles"
const val COL_COOLDOWN = "cooldown"
const val COL_COLOR = "color"

class Database(private val context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TITLE VARCHAR(256)," +
                "$COL_WARMUP INTEGER," +
                "$COL_WORK INTEGER," +
                "$COL_REST INTEGER," +
                "$COL_CYCLES INTEGER," +
                "$COL_COOLDOWN INTEGER," +
                "$COL_COLOR VARCHAR(256))"

        db?.execSQL(createTable)
    }

//    fun recreate() {
//        val dropTable = "DROP TABLE $TABLE_NAME"
//        val db = this.writableDatabase
//        db.execSQL(dropTable)
//
//        val createTable = "CREATE TABLE $TABLE_NAME (" +
//                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "$COL_TITLE VARCHAR(256)," +
//                "$COL_WARMUP INTEGER," +
//                "$COL_WORK INTEGER," +
//                "$COL_REST INTEGER," +
//                "$COL_CYCLES INTEGER," +
//                "$COL_COOLDOWN INTEGER," +
//                "$COL_COLOR VARCHAR(256))"
//
//        db.execSQL(createTable)
//    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    fun insert(workout : Workout) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COL_TITLE, workout.title)
        contentValues.put(COL_WARMUP, workout.warmup)
        contentValues.put(COL_WORK, workout.work)
        contentValues.put(COL_REST, workout.rest)
        contentValues.put(COL_CYCLES, workout.cycles)
        contentValues.put(COL_COOLDOWN, workout.cooldown)
        contentValues.put(COL_COLOR, workout.color)

        db.insert(TABLE_NAME, null, contentValues)
    }

    fun size() : Int {
        var result = 0
        val db = this.readableDatabase
        val query = "SELECT COUNT(1) FROM $TABLE_NAME"
        val queryResult = db.rawQuery(query, null)
        if (queryResult.moveToFirst()) {
            result = queryResult.getInt(0)
        }
        return result
    }

    fun getWorkoutByPosition(position: Int) : Workout {
        val result = Workout()

        if (position < size()) {
            val db = this.readableDatabase
            val query = "SELECT * FROM $TABLE_NAME ORDER BY $COL_ID LIMIT 1 OFFSET $position"
            val queryResult = db.rawQuery(query, null)
            if (queryResult.moveToFirst()) {
                result.id = queryResult.getInt(0)
                result.title = queryResult.getString(1)
                result.warmup = queryResult.getInt(2)
                result.work = queryResult.getInt(3)
                result.rest = queryResult.getInt(4)
                result.cycles = queryResult.getInt(5)
                result.cooldown = queryResult.getInt(6)
                result.color = queryResult.getString(7)
                return result
            }
        }
        return result
    }

    fun getWorkoutById(id : Int) : Workout {
        val result = Workout()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_ID IN ($id)"
        val queryResult = db.rawQuery(query, null)
        if (queryResult.moveToFirst()) {
            result.id = queryResult.getInt(0)
            result.title = queryResult.getString(1)
            result.warmup = queryResult.getInt(2)
            result.work = queryResult.getInt(3)
            result.rest = queryResult.getInt(4)
            result.cycles = queryResult.getInt(5)
            result.cooldown = queryResult.getInt(6)
            result.color = queryResult.getString(7)
        }

        return result
    }

    fun updateWorkout(workout: Workout) {
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val resultQuery = db.rawQuery(query, null)
        if (resultQuery.moveToFirst()) {
            var contentValues = ContentValues()
            contentValues.put(COL_TITLE, workout.title)
            contentValues.put(COL_WARMUP, workout.warmup)
            contentValues.put(COL_WORK, workout.work)
            contentValues.put(COL_REST, workout.rest)
            contentValues.put(COL_CYCLES, workout.cycles)
            contentValues.put(COL_COOLDOWN, workout.cooldown)
            contentValues.put(COL_COLOR, workout.color)

            db.update(TABLE_NAME, contentValues, "$COL_ID=?", arrayOf(workout.id.toString()))
        }
    }

    fun deleteWorkoutById(id : Int) {
        val db = this.writableDatabase

        db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(id.toString()))

        db.close()
    }

    fun clear() {
        val db = this.writableDatabase

        db.execSQL("DELETE FROM $TABLE_NAME")

        //db.close()
    }

}