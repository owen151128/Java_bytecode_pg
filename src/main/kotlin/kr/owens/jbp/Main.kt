package kr.owens.jbp

import java.io.File

/**
 * @author owen151128@gmail.com
 *
 * Created by owen151128 on 2021/11/17 00:12
 *
 * Providing features related to Main class
 */
const val APP_NAME = "Java bytecode pg"

fun main() {
    println("$APP_NAME Start!")

    val jBCPg = JavaBytecodePlayGround(File("classes.jar"), File("output.jar"))
    jBCPg.parseJar()

    println("$APP_NAME End!")
}
