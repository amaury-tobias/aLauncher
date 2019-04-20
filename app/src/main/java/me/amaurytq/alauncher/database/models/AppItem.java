package me.amaurytq.alauncher.database.models;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AppItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "package_label")
    public String packageLabel;

    @ColumnInfo(name = "package_name")
    public String packageName;

    @ColumnInfo(name = "is_hidden")
    public boolean isHidden;

    @ColumnInfo(name = "is_system_app")
    public boolean isSystemApp;
}