package com.sauryah.graion.domain.model.scripting

enum class ScriptLanguage(
    val id: String,
    val displayName: String,
    val fileExtension: String,
    val iconEmoji: String,
    val syntaxName: String
) {
    PYTHON(
        id = "python",
        displayName = "Python 3",
        fileExtension = ".py",
        iconEmoji = "🐍",
        syntaxName = "Python"
    ),
    JAVASCRIPT(
        id = "javascript",
        displayName = "JavaScript (ES6)",
        fileExtension = ".js",
        iconEmoji = "⚡",
        syntaxName = "JavaScript"
    ),
    LUA(
        id = "lua",
        displayName = "Lua 5.x",
        fileExtension = ".lua",
        iconEmoji = "🌙",
        syntaxName = "Lua"
    ),
    RUST_NATIVE(
        id = "rust",
        displayName = "Rust / Native C",
        fileExtension = ".rs",
        iconEmoji = "🦀",
        syntaxName = "Rust"
    )
}

data class ScriptPreset(
    val title: String,
    val category: String,
    val language: ScriptLanguage,
    val description: String,
    val code: String
)
