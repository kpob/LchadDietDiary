package pl.kpob.proccesor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName

/**
 * Created by kpob on 16.12.2017.
 */
data class CustomElement(val name: String, val mapName: String, val typeName: TypeName) {

    val defaultValue get() = when(typeName) {
        ClassName("kotlin", "String") -> "\"\""
        ClassName("kotlin", "Boolean") -> "false"
        ClassName("kotlin", "Int") -> "0"
        ClassName("kotlin", "Long") -> "0L"
        ClassName("kotlin", "Float") -> "0f"
        else -> ""
    }
}

data class MappingConfig(
        val hasFirebase: Boolean,
        val hasRepo: Boolean,
        val hasDomainModel: Boolean,
        val hasContract: Boolean
)