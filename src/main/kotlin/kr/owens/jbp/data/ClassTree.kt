package kr.owens.jbp.data

/**
 * @author owen151128@gmail.com
 *
 * Created by owen151128 on 2021/11/17 00:57
 *
 * Providing features related to ClassTree class
 */
data class ClassTree(
    var classWrapper: ClassWrapper,
    var parentClasses: MutableSet<String>,
    var subClasses: MutableSet<String>,
    var missingSuperClass: Boolean
)
