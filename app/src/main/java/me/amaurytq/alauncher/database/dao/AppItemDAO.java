package me.amaurytq.alauncher.database.dao;

import android.app.Application;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.FtsOptions;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import me.amaurytq.alauncher.database.models.AppItem;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AppItemDAO {

    @Query("SELECT * FROM appitem")
    List<AppItem> getAll();

    @Insert
    void insertAll(AppItem... appItems);

    @Delete
    void delete(AppItem appItem);

    @Update
    void update(AppItem appItem);

    @Insert(onConflict = REPLACE)
    void insert(AppItem appItem);
}
