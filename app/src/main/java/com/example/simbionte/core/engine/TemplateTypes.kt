package com.example.simbionte.core.engine

enum class FragmentRole { ABERTURA, CONECTOR, REFERENCIA, FECHO }

data class Fragment(
    val text: String,
    val role: FragmentRole,
    val category: String,
    val requiresContext: Boolean = false,
    val tone: String? = null
)

data class SlotDef(
    val role: FragmentRole,
    val category: String,
    val optional: Boolean = false
)

data class Template(
    val id: String,
    val slots: List<SlotDef>,
    val condition: (ResponseGenerator.GenContext) -> Boolean,
    val priority: Int = 0
)
