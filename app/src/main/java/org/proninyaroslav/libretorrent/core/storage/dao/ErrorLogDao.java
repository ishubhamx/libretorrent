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

package org.proninyaroslav.libretorrent.core.storage.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.proninyaroslav.libretorrent.core.model.data.entity.ErrorLog;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ErrorLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ErrorLog errorLog);

    @Query("SELECT * FROM ErrorLog ORDER BY timestamp DESC LIMIT :limit")
    Single<List<ErrorLog>> getRecentErrors(int limit);

    @Query("SELECT * FROM ErrorLog WHERE source = :source ORDER BY timestamp DESC LIMIT :limit")
    Single<List<ErrorLog>> getRecentErrorsBySource(String source, int limit);

    @Query("SELECT * FROM ErrorLog WHERE errorType = :errorType ORDER BY timestamp DESC LIMIT :limit")
    Single<List<ErrorLog>> getRecentErrorsByType(String errorType, int limit);

    @Query("DELETE FROM ErrorLog WHERE timestamp < :timestampBefore")
    int deleteOldErrors(long timestampBefore);

    @Query("DELETE FROM ErrorLog WHERE id = :id")
    int deleteById(long id);

    @Query("DELETE FROM ErrorLog")
    int deleteAll();

    @Query("SELECT COUNT(*) FROM ErrorLog")
    Flowable<Integer> observeErrorCount();

    @Query("SELECT * FROM ErrorLog WHERE id = :id")
    Single<ErrorLog> getById(long id);
}
