package com.mindera.skeletoid.logs.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.FileProvider
import com.mindera.skeletoid.generic.AndroidUtils
import com.mindera.skeletoid.logs.LOG
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * A way to share log file easily
 *
 *
 * Remember to add this to the Android Manifest of the App
 *
 *
 * <provider android:name="android.support.v4.content.FileProvider" android:authorities="${applicationId}" android:exported="false" android:grantUriPermissions="true">
 * <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/fileprovider"></meta-data>
 *  </provider>
 *
 *
 * and add the file fileprovider.xml to the resources with
 *
 * <paths>
 * <files-path path="." name="logs"></files-path>
</paths> *
 */
object ShareLogFilesUtils {

    private val TAG = "ShareLogFilesUtils"

    private val FOLDER_LOGS = "logs"
    private val FILE_LOG_ZIP = "logs.zip"

    /**
     * Class to be able to share LogFileAppender generated files
     *
     * @param activity           The activity
     * @param intentChooserTitle Intent chooser title
     * @param subjectTitle       Subject title (for email)
     * @param bodyText           Body text
     * @param emails             Emails to add on to: field (for email)
     * @param file               Log file to be sent
     */
    @JvmStatic
    public fun sendLogs(activity: Activity, intentChooserTitle: String, subjectTitle: String,
                        bodyText: String, emails: Array<String>?, file: File?) {


        val intent: Intent = Intent(Intent.ACTION_SEND)

        //TODO Currently this only supports 1 file. The code commented would support multiple.
        //        if (uris.size() == 1) {
        //        }
        //        else {
        //            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        //            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        //        }
        //        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        intent.putExtra(Intent.EXTRA_SUBJECT, subjectTitle)

        // Add emails to show on to: field
        if (emails != null && emails.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_EMAIL, emails)
        }

        // Add additional information to the email
        intent.putExtra(Intent.EXTRA_TEXT, bodyText)

        if (file != null) {
            val uri = FileProvider.getUriForFile(activity, activity.packageName, file)

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = activity.contentResolver.getType(uri)
        } else {
            intent.type = "text/plain"
        }
        activity.startActivity(Intent.createChooser(intent, intentChooserTitle))
    }

    /**
     * Class to be able to share compressed LogFileAppender generated files
     *
     * @param activity           The activity
     * @param intentChooserTitle Intent chooser title
     * @param subjectTitle       Subject title (for email)
     * @param bodyText           Body text
     * @param emails             Emails to add on to: field (for email)
     */
    @JvmStatic
    fun sendLogsEmail(activity: Activity, intentChooserTitle: String, subjectTitle: String,
                      bodyText: String, emails: Array<String>) {

        val output = File(getCompressedLogsPath(activity))
        if (!zipLogFiles(getFileLogPath(activity) + File.separator, output.absolutePath) || !output.exists()) {
            return
        }

        sendLogs(activity, intentChooserTitle, subjectTitle, bodyText, emails, output)
    }

    /**
     * Returns the path where the application logs are being stored.
     *
     * @param context Application context
     *
     * @return path   The default path where the application logs are being stored
     *
     * @see AndroidUtils
     */
    @JvmStatic
    fun getFileLogPath(context: Context): String {
        return AndroidUtils.getFileDirPath(context, "")
    }

    private fun getCompressedLogsPath(context: Context): String {
        val path = getFileLogPath(context) + File.separator + FOLDER_LOGS
        ensureFolderExists(path)

        return path + File.separator + FILE_LOG_ZIP
    }

    private fun ensureFolderExists(path: String) {
        File(path).mkdirs()
    }

    private fun zipLogFiles(source: String, output: String): Boolean {
        try {
            val fos = FileOutputStream(output)
            val zos = ZipOutputStream(fos)
            val srcFile = File(source)
            val files = srcFile.listFiles()

            LOG.d(TAG, "Compress directory: " + srcFile.name + " via zip.")
            for (file in files) {
                if (file.isDirectory) {
                    continue
                }

                LOG.d(TAG, "Adding file: " + file.name)
                val buffer = ByteArray(1024)
                val fis = FileInputStream(file)
                zos.putNextEntry(ZipEntry(file.name))

                var length = fis.read(buffer)
                while (length > 0) {
                    zos.write(buffer, 0, length)
                    length = fis.read(buffer)
                }

                zos.closeEntry()
                fis.close()
            }

            zos.close()
            return true
        } catch (ex: IOException) {
            LOG.e(TAG, "Unable to zip folder: " + ex.message)
            return false
        }

    }
}
