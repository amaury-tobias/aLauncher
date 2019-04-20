package me.amaurytq.alauncher.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import me.amaurytq.alauncher.database.dao.AppItemDAO;
import me.amaurytq.alauncher.database.models.AppItem;

@Database(entities = {AppItem.class}, version = 1)
public abstract class AppItemDatabase extends RoomDatabase {
    public abstract AppItemDAO appItemDAO();
}
