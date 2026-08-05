package com.xerra.attendancetracker.domain.usecase

import android.content.Context
import com.xerra.attendancetracker.data.database.AppDatabase
import java.io.FileInputStream
import java.io.OutputStream

class ExportDataUseCase(private val context: Context) {
    operator fun invoke(outputStream: OutputStream): Result<Unit> {
        return try {
            val db = AppDatabase.getDatabase(context)
            // Force a WAL checkpoint to write pending transactions from .wal to the main .db file
            try {
                db.openHelper.writableDatabase.run {
                    query("PRAGMA wal_checkpoint(FULL)").use { cursor ->
                        if (cursor.moveToFirst()) {
                            // Checkpoint ran successfully
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val dbFile = context.getDatabasePath("attendance_tracker_database")
            if (!dbFile.exists()) {
                return Result.failure(Exception("Database is empty or has not been created yet."))
            }

            FileInputStream(dbFile).use { input ->
                input.copyTo(outputStream)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
