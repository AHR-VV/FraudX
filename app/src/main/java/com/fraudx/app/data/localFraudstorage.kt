package com.fraudx.app.data

import android.content.Context
import com.fraudx.app.model.ManualRule
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

/** Stores user-created allow/block rules privately on this Android device. */
class LocalFraudStorage(context: Context) {
    private val file = File(context.filesDir, "fraudx_rules.json")
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun hasSavedRules(): Boolean = file.exists()

    fun saveRules(rules: List<ManualRule>) {
        file.writeText(json.encodeToString(rules))
    }

    fun loadRules(): List<ManualRule> {
        if (!file.exists()) return emptyList()
        return json.decodeFromString<List<ManualRule>>(file.readText())
    }
}
