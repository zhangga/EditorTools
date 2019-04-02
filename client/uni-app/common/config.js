// 和server工程的ExcelConfig.json保持一致
var ExcelConfig_json =
{
  "configs": [
    {
      "excel": "Quest.xlsx",
      "sheet": "任务|Quest",
      "redis_table": "QUEST",
      "redis_key": "sn",
	  "navigateTo": "../table/table"
    }
  ],
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
	}
}
