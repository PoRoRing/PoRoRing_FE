package com.example.pororing

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "pororing_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE MEMBER (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                age INTEGER NOT NULL,
                height_weight TEXT NOT NULL,
                job TEXT NOT NULL,
                health TEXT,
                allergy TEXT,
                etc TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS MEMBER")
        onCreate(db)
    }
}