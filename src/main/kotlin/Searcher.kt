import java.io.File
import java.io.FileNotFoundException

class Searcher {
    companion object {
        private const val maxLinesInHeader = 50
        private val predicates: Map<String, (String) -> Boolean> = mapOf( // Easy to add/change conditions
                "MIT" to { "MIT License" in it },
                "LGPL-3.0" to { "GNU Lesser General Public License" in it && "Version 3" in it},
                "BSD-3-Clause" to { "Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:" in it },
                "Apache-2.0" to { "Apache License, Version 2.0" in it },
                "GPL-3.0" to { "GNU General Public License" in it && "Version 3" in it }
        )

        fun searchLicenseInDirectory(file: File): Set<String> {
            val result: MutableSet<String> = HashSet()
            file.walk(FileWalkDirection.BOTTOM_UP).forEach {
                val res = searchLicenseInFile(it)
                if (res != "") {
                    result.add(res)
                }
            }
            return result
        }

        fun searchLicenseInFile(file: File): String {
            if (file.isDirectory) return ""
            val s: String
            try {
                s = file.useLines { lines: Sequence<String> ->
                    lines
                            .take(maxLinesInHeader) // We assume that header isn't more than 50 lines
                            .toList()
                }.joinToString("\n")
            } catch (e: FileNotFoundException) {
                return "" // Only for rare occasions (like symbolic links)
            }
            val result = predicates.filter { (_, predicate) -> predicate(s) }.keys.toList() // Find the license using map of predicates
            return if (result.isEmpty()) "" else result.first()
        }
    }
}