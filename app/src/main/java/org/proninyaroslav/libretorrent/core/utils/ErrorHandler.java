/*
 * Copyright (C) 2025 TorrentHub Fork
 *
 * This file is part of TorrentHub (LibreTorrent fork).
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.proninyaroslav.libretorrent.core.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.proninyaroslav.libretorrent.core.model.data.entity.ErrorLog;
import org.proninyaroslav.libretorrent.core.storage.AppDatabase;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Helper class for enhanced error handling with better user feedback and logging.
 * Provides utilities for network errors, parsing errors, and retry logic.
 */
public class ErrorHandler {
    private static final String TAG = ErrorHandler.class.getSimpleName();
    
    // Error type constants
    public static final String ERROR_TYPE_NETWORK = "NETWORK_ERROR";
    public static final String ERROR_TYPE_PARSE = "PARSE_ERROR";
    public static final String ERROR_TYPE_DATABASE = "DATABASE_ERROR";
    public static final String ERROR_TYPE_IO = "IO_ERROR";
    public static final String ERROR_TYPE_UNKNOWN = "UNKNOWN_ERROR";
    
    // Max errors to keep in database (older ones will be cleaned up)
    private static final int MAX_ERROR_LOGS = 1000;
    private static final long ERROR_LOG_RETENTION_DAYS = 7;

    /**
     * Log an error to the database for diagnostics and potential retry.
     * This is a fire-and-forget operation that runs asynchronously.
     */
    public static void logError(@NonNull Context context,
                                @NonNull String errorType,
                                @NonNull String errorMessage,
                                @Nullable Throwable throwable,
                                @NonNull String source,
                                int retryCount,
                                @Nullable String additionalData) {
        try {
            String stackTrace = null;
            if (throwable != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                stackTrace = sw.toString();
            }
            
            long timestamp = System.currentTimeMillis();
            
            String finalStackTrace = stackTrace;
            ErrorLog errorLog = new ErrorLog(
                0, // id will be auto-generated
                errorType,
                errorMessage,
                finalStackTrace,
                source,
                timestamp,
                retryCount,
                additionalData
            );
            
            // Insert asynchronously using RxJava
            io.reactivex.rxjava3.core.Completable.fromCallable(() -> {
                        AppDatabase.getInstance(context)
                            .errorLogDao()
                            .insert(errorLog);
                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        () -> {}, // onComplete
                        error -> Log.e(TAG, "Failed to insert error log", error)
                    );
            
            // Also log to Android logs for immediate debugging
            Log.e(TAG, String.format("[%s] %s: %s (retry: %d)", source, errorType, errorMessage, retryCount), throwable);
            
            // Clean up old errors periodically
            cleanupOldErrors(context);
            
        } catch (Exception e) {
            // Don't let error logging itself crash the app
            Log.e(TAG, "Failed to log error", e);
        }
    }

    /**
     * Get a user-friendly error message based on the error type.
     */
    @NonNull
    public static String getUserFriendlyMessage(@NonNull Context context, @NonNull String errorType, @Nullable Throwable throwable) {
        // This would ideally use string resources, but for simplicity using hardcoded messages
        switch (errorType) {
            case ERROR_TYPE_NETWORK:
                if (throwable != null && throwable.getMessage() != null) {
                    String msg = throwable.getMessage().toLowerCase();
                    if (msg.contains("429") || msg.contains("too many requests")) {
                        return "Rate limit exceeded. Please try again later.";
                    } else if (msg.contains("timeout")) {
                        return "Network request timed out. Please check your connection.";
                    } else if (msg.contains("unknown host") || msg.contains("unable to resolve")) {
                        return "Cannot reach server. Please check your internet connection.";
                    }
                }
                return "Network error occurred. Please check your internet connection.";
                
            case ERROR_TYPE_PARSE:
                return "Failed to parse data. The content may be malformed or in an unexpected format.";
                
            case ERROR_TYPE_DATABASE:
                return "Database error occurred. Please try restarting the app.";
                
            case ERROR_TYPE_IO:
                return "File operation failed. Please check storage permissions and available space.";
                
            case ERROR_TYPE_UNKNOWN:
            default:
                return "An unexpected error occurred. Please try again.";
        }
    }

    /**
     * Determine if an operation should be retried based on the error type and retry count.
     */
    public static boolean shouldRetry(@NonNull String errorType, int retryCount, int maxRetries) {
        if (retryCount >= maxRetries) {
            return false;
        }
        
        // Some error types are worth retrying
        switch (errorType) {
            case ERROR_TYPE_NETWORK:
                return true; // Network errors are often transient
            case ERROR_TYPE_IO:
                return retryCount < 2; // Limited retries for IO errors
            case ERROR_TYPE_DATABASE:
                return retryCount < 2; // Limited retries for database errors
            case ERROR_TYPE_PARSE:
                return false; // Parse errors usually won't fix themselves
            default:
                return retryCount < 1; // One retry for unknown errors
        }
    }

    /**
     * Get exponential backoff delay in milliseconds.
     */
    public static long getRetryDelay(int retryCount) {
        // Exponential backoff: 1s, 2s, 4s, 8s, etc.
        return (long) (1000 * Math.pow(2, retryCount));
    }

    /**
     * Clean up old error logs to prevent database bloat.
     */
    private static void cleanupOldErrors(@NonNull Context context) {
        try {
            long cutoffTime = System.currentTimeMillis() - (ERROR_LOG_RETENTION_DAYS * 24 * 60 * 60 * 1000);
            
            // Delete asynchronously using RxJava
            io.reactivex.rxjava3.core.Completable.fromCallable(() -> {
                        AppDatabase.getInstance(context)
                            .errorLogDao()
                            .deleteOldErrors(cutoffTime);
                        return true;
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        () -> {}, // onComplete
                        error -> Log.e(TAG, "Failed to cleanup old errors", error)
                    );
                
        } catch (Exception e) {
            Log.e(TAG, "Failed to cleanup old errors", e);
        }
    }
}
