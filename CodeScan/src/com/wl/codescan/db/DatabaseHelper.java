package com.wl.codescan.db;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wl.codescan.domain.Task;
import com.wl.codescan.domain.TaskGroup;
import com.wl.codescan.util.Logger;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private Dao<Task, Long> taskDao;
	private Dao<TaskGroup, Long> taskGroupDao;
	
	public DatabaseHelper(Context context) {
		super(context, DBConstants.DB_NAME, null, DBConstants.VERSION);
	}

	
	public void onCreate(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Task.class);
			TableUtils.createTable(connectionSource, TaskGroup.class);
		} catch (Exception e) {
			Logger.i("数据库创建失败");
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			List<Task> tasks=getTaskDao().queryForAll();
			List<TaskGroup> taskGroups=getTaskGroupDao().queryForAll();
			
			TableUtils.dropTable(connectionSource, Task.class, true);
			TableUtils.dropTable(connectionSource, TaskGroup.class, true);
			
			TableUtils.createTable(connectionSource, Task.class);
			TableUtils.createTable(connectionSource, TaskGroup.class);
			
			if (tasks!=null) {
				for(Task task:tasks){
					getTaskDao().create(task);
				}
			}
			if (taskGroups!=null) {
				for(TaskGroup group:taskGroups){
					getTaskGroupDao().create(group);
				}
			}
			Logger.e("数据库升级成功");
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.e(e.getMessage());
		}
		
	}

	public Dao<Task, Long> getTaskDao() {
		if (taskDao == null) {
			try {
				taskDao = getDao(Task.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return taskDao;
	}
	
	public Dao<TaskGroup, Long> getTaskGroupDao(){
		if (taskGroupDao==null) {
			try {
				taskGroupDao=getDao(TaskGroup.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return taskGroupDao;
	}
}
