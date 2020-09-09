import java.io.File
import java.io.FileNotFoundException

class Searcher {
    companion object {
        private const val maxLinesInHeader = 50

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
    }
}