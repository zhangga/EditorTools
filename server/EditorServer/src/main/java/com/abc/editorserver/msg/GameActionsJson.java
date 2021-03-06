package com.abc.editorserver.msg;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.abc.editorserver.module.excel.AddTableDataAction;
import com.abc.editorserver.module.excel.DeepCopyTableDataAction;
import com.abc.editorserver.module.excel.DeleteMultipleTableDataBySnAction;
import com.abc.editorserver.module.excel.DeleteTableDataBySnAction;
import com.abc.editorserver.module.excel.DownloadTableAction;
import com.abc.editorserver.module.excel.GetTableDataAtColumnsAction;
import com.abc.editorserver.module.excel.GetTableStatusAction;
import com.abc.editorserver.module.excel.GetVersionNumberBySnAction;
import com.abc.editorserver.module.excel.LockTableDataAction;
import com.abc.editorserver.module.excel.SubmitToSVNAction;
import com.abc.editorserver.module.excel.UnlockTableDataAction;
import com.abc.editorserver.module.excel.UpdateMultipleDataInSameColumnAction;
import com.abc.editorserver.module.excel.UpdateMultipleDataInSameRowAction;
import com.abc.editorserver.module.excel.UpdateTableDataFromSVNAction;
import com.abc.editorserver.module.hi.HiAction;
import com.abc.editorserver.module.hi.TestAction;
import com.abc.editorserver.module.excel.GetTableDataAction;
import com.abc.editorserver.module.excel.GetAllQuestBriefAction;
import com.abc.editorserver.module.excel.UpdateTableDataAction;
import com.abc.editorserver.module.excel.GetTableDataBySnAction;
import com.abc.editorserver.module.excel.GetQuestGoalInfoAction;
import com.abc.editorserver.module.excel.GetConditionInfoAction;

/**
 * 消息注册类
 *
 * @author U-Demon
 * @date 2018年5月26日 下午12:46:39
 */
public enum GameActionsJson {
	
	Hi("欢迎信息", 0, HiAction.class),
	Test("测试信息", 1, TestAction.class),
	GetTableData("获取表数据", 2, GetTableDataAction.class),
	GetAllQuestBrief("获取所有任务的简述", 3, GetAllQuestBriefAction.class),
	UpdateTableDataFromSVN("从SVN更新表格数据", 4, UpdateTableDataFromSVNAction.class),
	GetTableDataBySn("获取某条数据", 5, GetTableDataBySnAction.class),
	UpdateTableData("更新数据表数据", 6, UpdateTableDataAction.class),
	AddTableData("新增数据表数据", 7, AddTableDataAction.class),
	GetQuestGoalInfo("任务目标数据", 8, GetQuestGoalInfoAction.class),
	DownloadTableAction("下载表格数据", 9, DownloadTableAction.class),
	GetTableStatusAction("获取表格操作状态", 10, GetTableStatusAction.class),
	SubmitToSVNAction("更新改动至SVN", 11, SubmitToSVNAction.class),
	GetConditionInfoAction("获取任务条件数据", 12, GetConditionInfoAction.class),
	UpdateMultipleDataInSameRowAction("更新整条数据表数据", 13, UpdateMultipleDataInSameRowAction.class),
	DeleteTableDataBySnAction("删除某条数据表数据", 14, DeleteTableDataBySnAction.class),
	DeleteMultipleTableDataBySnAction("批量删除多条数据表数据", 15, DeleteMultipleTableDataBySnAction.class),
	UpdateMultipleDataInSameColumnAction("批量更新同一列下的多行数据", 16, UpdateMultipleDataInSameColumnAction.class),
	GetVersionNumberBySnAction("获取某条数据的最新版本号", 17, GetVersionNumberBySnAction.class),
	GetTableDataAtColumnsAction("获取指定表格在选定列的所有数据", 18, GetTableDataAtColumnsAction.class),
	LockTableDataAction("尝试获取指定表格指定数据的锁", 19, LockTableDataAction.class),
	UnlockTableDataAction("尝试为指定表格指定数据解锁", 20, UnlockTableDataAction.class),
	DeepCopyTableDataAction("深复制指定的表格数据", 21, DeepCopyTableDataAction.class)
	;
	
	/**
	 * 消息名
	 */
	private String name;
	
	/**
	 * 消息号
	 */
	private int id;
	
	/**
	 * 消息处理的Action
	 */
	private GameActionJson action = null;
	
	GameActionsJson(String name, int id, Class<? extends GameActionJson> clazz) {
		this.name = name;
		this.id = id;
		if (clazz != null) {
			try {
				this.action = clazz.getDeclaredConstructor().newInstance();
				this.action.setCmd(this.id);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Map<Integer, GameActionsJson> map = null;
	
	static {
		map = new HashMap<>();
		for (GameActionsJson ga : GameActionsJson.values()) {
			map.put(ga.id, ga);
		}
	}
	
	public static GameActionsJson getType(int cmd) {
		return map.get(cmd);
	}
	
	public static GameActionJson getAction(int cmd) {
		GameActionsJson type = map.get(cmd);
		if (type == null) {
			return null;
		}
		return type.action;
	}
	
	public GameActionJson getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

}
