package kr.owens.jbp.data

import org.objectweb.asm.tree.FieldNode

/**
 * @author owen151128@gmail.com
 *
 * Created by owen151128 on 2021/11/17 00:38
 *
 * Providing features related to FieldWrapper class
 */
data class FieldWrapper(
    var owner: ClassWrapper,
    var fieldNode: FieldNode,
    var originalName: String,
    var originalDescription: String
)
