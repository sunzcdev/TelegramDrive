package com.psaravan.filebrowserview.lib.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface FileBrowserDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(DriveFileEntity entity);

	@Query("select * from file_browser where id = :id")
	DriveFileEntity getFile(long id);

	@Query("select * from file_browser where dirId = :dirId order by type")
	List<DriveFileEntity> list(long dirId);

	@Query("select * from file_browser where dirId = :dirId and type = 1 order by name")
	List<DriveFileEntity> listFiles(long dirId);

	@Query("select * from file_browser where dirId = :dirId and type = 0 order by name")
	List<DriveFileEntity> listDirs(long dirId);

	@Query("select * from file_browser where id = (select dirId from file_browser where id = (select dirId from file_browser where id = :id))")
	List<DriveFileEntity> listParents(long id);

	@Update()
	void update(DriveFileEntity entity);

	@Delete
	void delete(DriveFileEntity entity);

	@Query("select * from file_browser")
	List<DriveFileEntity> listAll();

	@Query("select * from file_browser where remoteFileId = :remoteId")
	DriveFileEntity getRemoteFile(String remoteId);
}
