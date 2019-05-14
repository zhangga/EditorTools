package com.abc.editorserver.msg;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import com.abc.editorserver.module.excel.AddTableDataAction;
import com.abc.editorserver.module.excel.DownloadTableAction;
import com.abc.editorserver.module.excel.GetTableStatusAction;
import com.abc.editorserver.module.excel.SubmitToSVNAction;
import com.abc.editorserver.module.excel.UpdateMultipleTableDataAction;
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
	UpdateMultipleTableDataAction("更新整条数据表数据", 13, UpdateMultipleTableDataAction.class)
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
