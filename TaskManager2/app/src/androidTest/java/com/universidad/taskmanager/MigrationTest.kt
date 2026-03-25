package com.universidad.taskmanager

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.universidad.taskmanager.data.local.AppDatabase
import com.universidad.taskmanager.data.local.MIGRATION_1_2
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    fun migrate1To2_preservesDataAndAddsDefaultPriority() {
        // 1. Crear BD en versión 1 e insertar fila de prueba
        helper.createDatabase("migration-test.db", 1).apply {
            execSQL(
                "INSERT INTO tasks (title, description, isCompleted, createdAt) " +
                        "VALUES ('Tarea Test', 'Descripción', 0, 1000)"
            )
            close()
        }

        // 2. Ejecutar migración 1→2
        val db = helper.runMigrationsAndValidate(
            "migration-test.db", 2, true, MIGRATION_1_2
        )

        // 3. Verificar que el dato original persiste con priority = 1
        val cursor = db.query("SELECT title, priority FROM tasks WHERE title = 'Tarea Test'")
        cursor.moveToFirst()
        assertEquals("Tarea Test", cursor.getString(0))
        assertEquals(1, cursor.getInt(1))  // DEFAULT 1 aplicado
        cursor.close()
    }
}