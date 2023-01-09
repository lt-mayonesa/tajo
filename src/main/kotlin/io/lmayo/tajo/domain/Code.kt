package io.lmayo.tajo.domain

class Code(token: String) {
    private val original: String = token
    val token: String = token
        get() = field.lowercase()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Code

        if (original != other.original) return false

        return true
    }

    override fun hashCode(): Int {
        return original.hashCode()
    }

    override fun toString(): String {
        return original
    }

}
