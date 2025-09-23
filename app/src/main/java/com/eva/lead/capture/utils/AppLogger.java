package com.eva.lead.capture.utils;

/**
 * Created by Laxmi Kant Joshi on 05/09/2025
 */

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AppLogger {
    private static final String LOG_TAG = "AppLogger";
    private static final String LOG_FILE_NAME = "app_log.log";
    private static final long MAX_LOG_SIZE = 7 * 1024 * 1024; // 7MB
    private static final int BATCH_SIZE = 100; // Number of messages per batch
    private final File logFile;
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final Thread logThread;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

    public AppLogger(Context context) {
//        this.logFile = new File(context.getFilesDir(), LOG_FILE_NAME);
        this.logFile = new File(context.getExternalFilesDir(null), LOG_FILE_NAME);
        this.logThread = new Thread(new LogWriter());
        logThread.start();
    }

    public void d(String tag, String message) {
        log(Log.DEBUG, tag, message);
    }

    public void v(String tag, String message) {
        log(Log.VERBOSE, tag, message);
    }

    public void i(String tag, String message) {
        log(Log.INFO, tag, message);
    }

    public void w(String tag, String message) {
        log(Log.WARN, tag, message);
    }

    public void e(String tag, String message) {
        log(Log.ERROR, tag, message);
    }

    private void log(int priority, String tag, String message) {
//        if (priority >= Log.INFO) { // Adjust log level for production
        Log.println(priority, tag, message);
//        }
        String logMessage = formatLogMessage(priority, tag, message);
        try {
            logQueue.put(logMessage);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Failed to put log message into queue", e);
        }
//        Log.println(priority, tag, message);
//        String logMessage = formatLogMessage(priority, tag, message);
//        try {
//            logQueue.put(logMessage);
//        } catch (InterruptedException e) {
//            Log.e(LOG_TAG, "Failed to put log message into queue", e);
//        }
    }

    private String formatLogMessage(int priority, String tag, String message) {
        String timestamp = simpleDateFormat.format(new Date());
        long threadId = Thread.currentThread().getId();
        String logLevel = getLogLevel(priority);
        return String.format("%s %s %s: %s", timestamp, /*threadId,*/ logLevel, tag, message);
    }

    private String getLogLevel(int priority) {
        switch (priority) {
            case Log.VERBOSE:
                return "V";
            case Log.DEBUG:
                return "D";
            case Log.INFO:
                return "I";
            case Log.WARN:
                return "W";
            case Log.ERROR:
                return "E";
            default:
                return "U"; // Unknown
        }
    }

    private class LogWriter implements Runnable {

        @Override
        public void run() {
            try (FileWriter writer = new FileWriter(logFile, true)) {
                while (true) {
                    StringBuilder batch = new StringBuilder();
                    for (int i = 0; i < BATCH_SIZE; i++) {
                        String logMessage = logQueue.poll(1, TimeUnit.SECONDS);
                        if (logMessage != null) {
                            batch.append(logMessage).append("\n");
                        } else {
                            break;
                        }
                    }
                    if (batch.length() > 0) {
                        if (logFile.length() >= MAX_LOG_SIZE) {
                            rotateLog();
                        }
                        writer.append(batch.toString());
                        writer.flush();
                    }
                }
            } catch (IOException | InterruptedException e) {
                Log.e(LOG_TAG, "Failed to write log message to file", e);
            }
        }

        private void rotateLog() throws IOException {
            File oldLogFile = new File(logFile.getPath() + ".1");
            if (oldLogFile.exists()) {
                oldLogFile.delete();
            }
            logFile.renameTo(oldLogFile);
            logFile.createNewFile();
        }
    }
}
