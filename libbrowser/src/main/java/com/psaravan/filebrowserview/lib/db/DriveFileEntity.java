package com.psaravan.filebrowserview.lib.db;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "file_browser")
public class DriveFileEntity {
	@PrimaryKey(autoGenerate = true)
	public long id;
	public long dirId;
	public String remoteFileId;
	public int type;
	public String name;
	public long length;
	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	public Date createTime;

	@Override
	public String toString() {
		return "DriveFileEntity{" +
				"id=" + id +
				", remoteFileId='" + remoteFileId + '\'' +
				", dirId=" + dirId +
				", type=" + type +
				", name='" + name + '\'' +
				", length=" + length +
				", createTime=" + createTime +
				'}';
	}

	public boolean isFile() {
		return type == 1;
	}
}
