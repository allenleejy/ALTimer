import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import android.content.Context
import java.io.InputStreamReader

object AlgorithmReader {
    fun readOLL(context: Context): ArrayList<String> {
        val algorithmLines = ArrayList<String>()
        try {
            val inputStream = context.assets.open("OLL.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                algorithmLines.add(line!!)
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return algorithmLines
    }
    fun readPLL(context: Context): ArrayList<String> {
        val algorithmLines = ArrayList<String>()
        try {
            val inputStream = context.assets.open("PLL.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                algorithmLines.add(line!!)
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return algorithmLines
    }
}
