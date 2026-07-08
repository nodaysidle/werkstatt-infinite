package com.gift.werkstatt.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gift.werkstatt.data.local.dao.CanvasDao
import com.gift.werkstatt.data.local.entity.CanvasEntity

@Database(
    entities = [CanvasEntity::class],
    version = 3,
    exportSchema = false
)
abstract class WerkstattDatabase : RoomDatabase() {
    abstract fun canvasDao(): CanvasDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) = Unit
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE canvas_entries ADD COLUMN gridMode TEXT NOT NULL DEFAULT 'None'")
                db.execSQL("ALTER TABLE canvas_entries ADD COLUMN gridSize REAL NOT NULL DEFAULT 40.0")
            }
        }
    }
}
