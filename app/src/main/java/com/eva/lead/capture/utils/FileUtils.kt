package com.eva.lead.capture.utils

import android.content.Context
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class FileUtils {
    companion object {

        fun createZipFileOfLeads(
            context: Context,
            leadList: List<EvaLeadData>?,
            zipFileName: String = "exported_leads.zip"
        ): File? {

            val filesToZip: List<File> = getFileFromLeadDetails(context, leadList)

            if (filesToZip.isEmpty()) {
                context.showToast("No files to zip", ToastType.INFO)
                return null
            }

            try {
                // Create "exports" folder inside app's external files directory
                val exportDir = context.getExternalFolderPath("exports")

                val zipFile = File(exportDir, zipFileName)
                val fos = FileOutputStream(zipFile)
                val zos = ZipOutputStream(BufferedOutputStream(fos))

                val buffer = ByteArray(1024)

                for (file in filesToZip) {
                    if (file.exists()) {
                        val fis = FileInputStream(file)
                        val entry = ZipEntry(file.name)
                        zos.putNextEntry(entry)

                        var length: Int
                        while (fis.read(buffer).also { length = it } > 0) {
                            zos.write(buffer, 0, length)
                        }

                        zos.closeEntry()
                        fis.close()
                    }
                }

                zos.close()
                fos.close()
                context.showToast("ZIP created at: ${zipFile.absolutePath}", ToastType.SUCCESS)
                return zipFile

            } catch (e: Exception) {
                e.printStackTrace()
                context.showToast("Failed to create ZIP: ${e.message}", ToastType.ERROR)
                return null
            }
        }

        private fun getFileFromLeadDetails(context: Context, leadList: List<EvaLeadData>?): List<File> {
            val list = mutableListOf<File>()
            if (!leadList.isNullOrEmpty()) {
                val csvFile = exportLeadsToCSV(context, leadList)
                if (csvFile != null) {
                    list.add(csvFile)
                }
                for (lead in leadList) {
                    val imageFile = lead.imageFileNames
                    if (!imageFile.isNullOrEmpty()) {
                        val imageDir = context.getExternalFolderPath("clicked_image")
                        val imageArr = imageFile.split(",")
                        for (imageName in imageArr) {
                            val file = File(imageDir, imageName)
                            if (file.exists()) {
                                list.add(file)
                            }
                        }
                    }

                    val audioFile = lead.audioFilePath
                    if (!audioFile.isNullOrEmpty()) {
                        val audioDir =  context.getExternalFolderPath("recording")
                        val audioFile = File(audioDir, audioFile)
                        list.add(audioFile)
                    }
                }
            }
            return list
        }

        fun exportLeadsToCSV(mContext: Context, leadList: List<EvaLeadData>?): File? {
            if (leadList.isNullOrEmpty()) {
                mContext.showToast("No leads to export}", ToastType.INFO)
                return null
            }

            try {
                // Directory to save CSV
                val exportDir = mContext.getExternalFolderPath("export")

                val csvFile = File(exportDir, "leads_${System.currentTimeMillis()}.csv")
                val writer = FileWriter(csvFile)

                // CSV Header
                writer.append(
                    "id,lead_id,tag,first_name,last_name,email,phone,designation,company_name,additional_info,notes,images,audio_file,timestamp,quicknote,questionanswer\n"
                )

                // CSV Rows
                for (lead in leadList.reversed()) {
                    writer.append("${lead.id ?: ""},")
                    writer.append("${lead.leadId ?: ""},")
                    writer.append("${lead.tag ?: ""},")
                    writer.append("${lead.firstName ?: ""},")
                    writer.append("${lead.lastName ?: ""},")
                    writer.append("${lead.email ?: ""},")
                    writer.append("${lead.phone ?: ""},")
                    writer.append("${lead.designation ?: ""},")
                    writer.append("${lead.companyName ?: ""},")
                    writer.append("${lead.additionalInfo ?: ""},")
                    writer.append("${lead.notes ?: ""},")
                    writer.append("${lead.imageFileNames ?: ""},")
                    writer.append("${lead.audioFilePath ?: ""},")
                    writer.append("${lead.timestamp ?: ""},")
                    writer.append("${lead.quickNote ?: ""},")
                    writer.append("${lead.questionAnswer ?: ""}\n")
                }

                writer.flush()
                writer.close()

                mContext.showToast("CSV exported to: ${csvFile.absolutePath}", ToastType.SUCCESS)
                return csvFile
            } catch (e: Exception) {
                e.printStackTrace()
                mContext.showToast("Failed to export CSV: ${e.message}", ToastType.ERROR)
            }
            return null
        }
    }
}