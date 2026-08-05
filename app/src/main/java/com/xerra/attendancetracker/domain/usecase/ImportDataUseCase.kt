package com.xerra.attendancetracker.domain.usecase

import android.content.Context
import com.xerra.attendancetracker.data.database.AppDatabase
import java.io.FileOutputStream
import java.io.InputStream

class ImportDataUseCase(private val context: Context) {
    operator fun invoke(inputStream: InputStream): Result<Unit> {
        return try {
            val db = AppDatabase.getDatabase(context)
            db.close()

            val dbFile = context.getDatabasePath("attendance_tracker_database")
            val walFile = context.getDatabasePath("attendance_tracker_database-wal")
            val shmFile = context.getDatabasePath("attendance_tracker_database-shm")

            if (walFile.exists()) walFile.delete()
            if (shmFile.exists()) shmFile.delete()

            FileOutputStream(dbFile).use { output ->
                inputStream.copyTo(output)
            }

            // Force database initialization to verify the import was correct
            val newDb = AppDatabase.getDatabase(context)
            newDb.openHelper.writableDatabase

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
