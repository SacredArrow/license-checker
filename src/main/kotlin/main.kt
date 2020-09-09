import java.io.File

fun searchLicenseInDirectory(file: File): Set<String> {
    val result: MutableSet<String> = HashSet()
    file.walk(FileWalkDirection.BOTTOM_UP).forEach {
        val res = searchLicenseInFile(it)
        if (res != "") {
            result.add(res)
        }}
    return result
}

fun searchLicenseInFile(file: File): String {
    if (file.isDirectory) return ""
    val s = file.useLines { lines: Sequence<String> ->
        lines
                .take(50) // We assume that header isn't more than 50 lines
                .toList()
    }.joinToString("\n")
    return when { // Here we consider distinctive parts of licenses in question.
        "GNU General Public License" in s && "Version 3" in s -> {
            "GPL-3.0"
        }
        "MIT License" in s -> {
            "MIT"
        }
        "GNU Lesser General Public License" in s && "Version 3" in s -> {
            "LGPL-3.0"
        }
        "Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:" in s -> { //TODO something better?
            "BSD-3-Clause"
        }
        "Apache License, Version 2.0" in s -> {
            "Apache-2.0"
        }
        else -> {
            ""
        }
    }
}

fun main(args: Array<String>) {
    print("Please, enter full path to directory: ")
    var path = readLine()!!
    if (path.last() != '/') {
        path += '/'
    }
    val mainLicenseFile = when { // Look at main license file
        File("${path}LICENSE").exists() -> {
            File("${path}LICENSE")
        }
        File("${path}LICENSE.txt").exists() -> {
            File("${path}LICENSE.txt")
        }
        else -> {
            null
        }
    }
    if (mainLicenseFile != null) {
        val result = searchLicenseInFile(mainLicenseFile)
        if (result == "") {
            println("There is main license in project, but it it cannot be recognised.")
        } else {
            println("Main license of the project is $result.")
        }
    } else {
        println("No file found for main license.")
    }
    val list = searchLicenseInDirectory(File(path)).toTypedArray()
    when {
        list.isEmpty() -> {
            print("Project doesn't contain other licenses.")
        }
        list.size == 1 -> {
            print("Project also contains ${list[0]} license in other files.")

        }
        else -> { // Pretty print for all licenses
            print("Project also contains ")
            for (el in list.dropLast(2)) {
                print("$el, ")
            }
            print("${list[list.size - 2]} and ${list[list.size - 1]} licenses in other files.")
        }
    }
}