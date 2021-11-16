package kr.owens.jbp

import kr.owens.jbp.data.ClassTree
import kr.owens.jbp.data.ClassWrapper
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * @author owen151128@gmail.com
 *
 * Created by owen151128 on 2021/11/20 12:58
 *
 * Providing features related to JavaBytecodePlayGround class
 */
class JavaBytecodePlayGround(private val inputJar: File, private val outputJar: File) {
    companion object {
        const val DOT_CLASS = ".class"
        const val EMPTY = ""
    }

    private val classes = mutableMapOf<String, ClassNode>()
    private val classPath = mutableMapOf<String, ClassWrapper>()
    private val hierarchy = mutableMapOf<String, ClassTree>()
    private val classDataMap = mutableMapOf<String, ByteArray>()
    private val mappings = mutableMapOf<String, String>()
    private val classWrappers = mutableListOf<ClassWrapper>()

    private val inputJarStream by lazy { ZipInputStream(inputJar.inputStream().buffered()) }
    private val outputJarStream by lazy {
        ZipOutputStream(outputJar.outputStream().buffered()).apply {
            setMethod(ZipOutputStream.DEFLATED)
        }
    }

    fun parseJar() {
        while (true) {
            val entry = inputJarStream.nextEntry ?: break

            if (entry.isDirectory) {
                outputJarStream.putNextEntry(entry)
                continue
            }

            val entryData = inputJarStream.readBytes()

            entry.name.also {
                if (it.endsWith(DOT_CLASS)) {
                    val classReader = ClassReader(entryData)
                    val classNode = ClassNode()

                    classReader.accept(classNode, 0)
                    classes[it] = classNode
                    classDataMap[it] = entryData
                }
            }
        }

        classes.forEach {
            classPath[it.key.replace(DOT_CLASS, EMPTY)] =
                ClassWrapper(it.value, originalClass = classDataMap[it.key])
        }

        println("Build hierarchy...")

        for (node in classes.values) {
            val classWrapper = ClassWrapper(node, originalClass = byteArrayOf())
            classWrappers.add(classWrapper)
            buildHierarchy(classWrapper, null, false)
        }

        println("Finish building hierarchy!")
    }

    private fun buildHierarchy(
        classWrapper: ClassWrapper,
        subClass: ClassWrapper?,
        acceptMissingClass: Boolean
    ) {
        if (hierarchy[classWrapper.classNode.name] == null) {
            val classTree = ClassTree(classWrapper)

            classWrapper.classNode.superName?.let {
                classTree.parentClasses.add(classWrapper.classNode.superName)
                val superClass = classPath[classWrapper.classNode.superName]

                if (superClass == null) {
                    if (!acceptMissingClass) {
                        classTree.missingSuperClass = true
                        println("Missing class : ${classWrapper.classNode.superName}")
                    } else {
                        throw Exception("Missing super class :  ${classWrapper.classNode.superName} / ${classWrapper.classNode.name} in the classpath")
                    }
                } else {
                    buildHierarchy(superClass, classWrapper, acceptMissingClass)

                    hierarchy[classWrapper.classNode.superName]?.let {
                        if (it.missingSuperClass) {
                            classTree.missingSuperClass = true
                        }
                    }
                }
            }

            classWrapper.classNode.interfaces?.let {
                if (it.isNotEmpty()) {
                    for (s in it) {
                        classTree.parentClasses.add(s)
                        val interfaceClass = classPath[s]

                        if (interfaceClass == null) {
                            if (!acceptMissingClass) {
                                classTree.missingSuperClass = true
                                println("Missing interface class : $s")
                            } else {
                                throw Exception("Missing interface class :  ${classWrapper.classNode.name} in the classpath")
                            }
                        } else {
                            buildHierarchy(interfaceClass, classWrapper, acceptMissingClass)

                            hierarchy[s]?.let { ct ->
                                if (ct.missingSuperClass) {
                                    classTree.missingSuperClass = true
                                }
                            }
                        }
                    }
                }
            }
            hierarchy[classWrapper.classNode.name] = classTree
        }
        if (subClass != null) {
            hierarchy[classWrapper.classNode.name]?.subClasses?.add(subClass.classNode.name)
        }
    }
}