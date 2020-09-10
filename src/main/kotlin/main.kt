import java.io.File


fun main() {
    print("Please, enter full path to directory: ")

    var path = readLine()!!
    if (path.last() != '/') {
        path += '/'
    }

    val mainLicenseFile = when { // Look at main license file (if exists)
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
        val result = Searcher.searchLicenseInFile(mainLicenseFile)
        if (result == "") {
            println("There is main license in project, but it it cannot be recognised.")
        } else {
            println("Main license of the project is $result.")
        }
    } else {
        println("No file found for main license.")
    }

    val licenses = Searcher.searchLicenseInDirectory(File(path)).toTypedArray()
    when { // Pretty print for all licenses
        licenses.isEmpty() -> {
            print("Project doesn't contain other licenses.")
        }
        licenses.size == 1 -> {
            print("Project also contains ${licenses.first()} license in other files.")
        }
        else -> {
            print("Project also contains ")
            print(licenses.dropLast(1).joinToString(separator = ", "))
            print(" and ${licenses.last()} licenses in other files.")
        }
    }
}