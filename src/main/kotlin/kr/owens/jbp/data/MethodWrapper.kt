package kr.owens.jbp.data

import org.objectweb.asm.tree.MethodNode

/**
 * @author owen151128@gmail.com
 *
 * Created by owen151128 on 2021/11/17 00:36
 *
 * Providing features related to MethodWrapper class
 */
data class MethodWrapper(
    var owner: ClassWrapper,
    var methodNode: MethodNode,
    var originalName: String,
    var originalDescription: String
)