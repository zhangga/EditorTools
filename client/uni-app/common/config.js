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
			"navigateTo": "../table/table",
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
			"navigateTo": "../table/table",
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
			"navigateTo": "../table/table",
    }
  ],
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
