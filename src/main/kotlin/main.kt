import java.io.File


fun main(args: Array<String>) {
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

    val list = Searcher.searchLicenseInDirectory(File(path)).toTypedArray()
    when { // Pretty print for all licenses
        list.isEmpty() -> {
            print("Project doesn't contain other licenses.")
        }
        list.size == 1 -> {
            print("Project also contains ${list[0]} license in other files.")

        }
        else -> {
            print("Project also contains ")
            print(list.dropLast(1).joinToString(separator = ", "))
            print(" and ${list.last()} licenses in other files.")
        }
    }
}