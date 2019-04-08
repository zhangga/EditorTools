// 和server工程的ExcelConfig.json保持一致
var ExcelConfig_json =
{
  "configs": [
    {
      // excel表名
      "excel": "Quest.xlsx",
      // sheet名
      "sheet": "任务|Quest",
      // Redis hash的表名
      "redis_table": "QUEST",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../quest/quest"
    },
    {
      // excel表名
      "excel": "Quest.xlsx",
      // sheet名
      "sheet": "任务目标|QuestGoal",
      // Redis hash的表名
      "redis_table": "QUESTGOAL",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../table/table"
    },
    {
      // excel表名
      "excel": "Quest.xlsx",
      // sheet名
      "sheet": "对话表|Plot",
      // Redis hash的表名
      "redis_table": "PLOT",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../table/table"
    }
  ],
  "triggers": [
//    {
//      // excel表名
//      "excel": "Trigger.xlsx",
//      // sheet名
//      "sheet": "ERegionTriggerType"
//    },
//    {
//      // excel表名
//      "excel": "Trigger.xlsx",
//      // sheet名
//      "sheet": "EItemUseScript"
//    },
    {
      // excel表名
      "excel": "Trigger.xlsx",
      // sheet名
      "sheet": "EConditionType"
    },
//    {
//      // excel表名
//      "excel": "Trigger.xlsx",
//      // sheet名
//      "sheet": "EnumQuestGoalType"
//    },
//    {
//      // excel表名
//      "excel": "Trigger.xlsx",
//      // sheet名
//      "sheet": "EActionType"
//    }
  ],
  // 任务类型
  "EnumQuestType": ["1", "主线任务", "2", "结局任务", "3", "关系任务", "4", "奇遇任务"],
  // 任务页签
  "QuestTab": ["任务属性", "任务接取", "任务目标", "任务完成"],
  // 基础配置
  "defaultNames": [
    "cs",
    "type",
    "enName",
    "cnName"
  ]
};
	
export default {
	ExcelConfig() {
		return ExcelConfig_json;
	},
	GetExcelConfig(table) {
		for (var i = 0; i < ExcelConfig_json['configs'].length; i++) {
			var config = ExcelConfig_json['configs'][i];
			if (config['redis_table'] == table) {
				return config;
			}
		}
		return null;
	},
	GetExcelConfig(excelName, sheetName) {
		for (var i = 0; i < ExcelConfig_json['configs'].length; i++) {
			var config = ExcelConfig_json['configs'][i];
			if (config['excel'] == excelName && config['sheet'] == sheetName) {
				return config;
			}
		}
	}
}
