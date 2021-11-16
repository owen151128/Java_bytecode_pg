package kr.owens.jbp.data

import org.objectweb.asm.tree.ClassNode

/**
 * @author owen151128@gmail.com
 *
 * Created by owen151128 on 2021/11/17 00:31
 *
 * Providing features related to ClassWrapper class
 */
data class ClassWrapper(
    var classNode: ClassNode,
    var originalName: String = "",
    var originalClass: ByteArray,
    val methods: MutableList<MethodWrapper>,
    var fields: MutableList<FieldWrapper>
) {
    fun initializeClassWrapper() {
        originalName = classNode.name

        methods.run {
            classNode.methods.forEach {
                add(MethodWrapper(this@ClassWrapper, it, it.name, it.desc))
            }
        }

        classNode.fields.let {
            fields.run {
                it.forEach {
                    add(FieldWrapper(this@ClassWrapper, it, it.name, it.desc))
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassWrapper

        if (classNode != other.classNode) return false
        if (originalName != other.originalName) return false
        if (!originalClass.contentEquals(other.originalClass)) return false
        if (methods != other.methods) return false
        if (fields != other.fields) return false

        return true
    }

    override fun hashCode(): Int {
        var result = classNode.hashCode()
        result = 31 * result + originalName.hashCode()
        result = 31 * result + originalClass.contentHashCode()
        result = 31 * result + methods.hashCode()
        result = 31 * result + fields.hashCode()
        return result
    }
}
