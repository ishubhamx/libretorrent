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

package org.proninyaroslav.libretorrent.core.model.data.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity for storing error logs to help with diagnostics and retry logic.
 * Helps track network errors, parsing errors, and other issues.
 */
@Entity
public class ErrorLog {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    @NonNull
    public final String errorType; // e.g., "NETWORK_ERROR", "PARSE_ERROR", "DATABASE_ERROR"
    
    @NonNull
    public final String errorMessage;
    
    @Nullable
    public final String stackTrace;
    
    @NonNull
    public final String source; // e.g., "FeedDownload", "TorrentEngine"
    
    public final long timestamp; // Unix timestamp
    
    public final int retryCount; // Number of times operation was retried
    
    @Nullable
    public final String additionalData; // JSON string with extra context

    public ErrorLog(long id,
                    @NonNull String errorType,
                    @NonNull String errorMessage,
                    @Nullable String stackTrace,
                    @NonNull String source,
                    long timestamp,
                    int retryCount,
                    @Nullable String additionalData) {
        this.id = id;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
        this.source = source;
        this.timestamp = timestamp;
        this.retryCount = retryCount;
        this.additionalData = additionalData;
    }

    @NonNull
    @Override
    public String toString() {
        return "ErrorLog{" +
                "id=" + id +
                ", errorType='" + errorType + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", source='" + source + '\'' +
                ", timestamp=" + timestamp +
                ", retryCount=" + retryCount +
                '}';
    }
}
