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
			},
			{
				// excel表名
				"excel": "CharacterMonster.xlsx",
				// sheet名
				"sheet": "怪物|Character",
				// Redis hash的表名
				"redis_table": "CHARACTER",
				// Redis hash的key值
				"redis_key": "sn",
				// 点击跳转的导航页
				"navigateTo": "../table/table"
			},
			{
				// excel表名
				"excel": "Item.xlsx",
				// sheet名
				"sheet": "物品|Item",
				// Redis hash的表名
				"redis_table": "ITEM",
				// Redis hash的key值
				"redis_key": "sn",
				// 点击跳转的导航页
				"navigateTo": "../table/table"
			},
			{
				// excel表名
				"excel": "Npc.xlsx",
				// sheet名
				"sheet": "Npc|Npc",
				// Redis hash的表名
				"redis_table": "NPC",
				// Redis hash的key值
				"redis_key": "sn",
			// 点击跳转的导航页
				"navigateTo": "../table/table"
			},
		{
			// excel表名
			"excel": "Action.xlsx",
			// sheet名
			"sheet": "行为触发|Action",
			// Redis hash的表名
			"redis_table": "ACTION",
			// Redis hash的key值
			"redis_key": "sn",
			// 点击跳转的导航页
			"navigateTo": "../table/table"
		},
			{
				// excel表名
				"excel": "DropConfig.xlsm",
				// sheet名
				"sheet": "掉落组|DropGroup",
				// Redis hash的表名
				"redis_table": "DROPGROUP",
				// Redis hash的key值
				"redis_key": "sn",
			// 点击跳转的导航页
				"navigateTo": "../table/table"
			},
		{
				// excel表名
				"excel": "Condition.xlsx",
				// sheet名
				"sheet": "条件|Condition",
				// Redis hash的表名
				"redis_table": "CONDITION",
				// Redis hash的key值
				"redis_key": "sn",
				// 点击跳转的导航页
				"navigateTo": "../table/table"
			},
		{
			// excel表名
			"excel": "CharacterClass.xlsx",
			// sheet名
			"sheet": "职业|CharacterClass",
			// Redis hash的表名
			"redis_table": "SEX",
			// Redis hash的key值
			"redis_key": "sn",
			// 点击跳转的导航页
			"navigateTo": "../table/table"
		},
		{
			// excel表名
			"excel": "Career.xlsx",
			// sheet名
			"sheet": "职业|Career",
			// Redis hash的表名
			"redis_table": "OCCUPATION",
			// Redis hash的key值
			"redis_key": "sn",
			// 点击跳转的导航页
			"navigateTo": "../table/table"
		},
		{
      // excel表名
      "excel": "Skill.xlsm",
      // sheet名
      "sheet": "技能|Skill",
      // Redis hash的表名
      "redis_table": "SKILL",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../table/table"
    },
    {
      // excel表名
      "excel": "Buff.xlsm",
      // sheet名
      "sheet": "Buff|Buff",
      // Redis hash的表名
      "redis_table": "BUFF",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../table/table"
    },
    {
      // excel表名
      "excel": "Bullet.xlsm",
      // sheet名
      "sheet": "子弹|Bullet",
      // Redis hash的表名
      "redis_table": "BULLET",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../table/table"
    },
    {
      // excel表名
      "excel": "Skill.xlsm",
      // sheet名
      "sheet": "技能效果|HitEffect",
      // Redis hash的表名
      "redis_table": "HITEFFECT",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../table/table"
    },
    {
      // excel表名
      "excel": "Skill.xlsm",
      // sheet名
      "sheet": "效果|Effect",
      // Redis hash的表名
      "redis_table": "EFFECT",
      // Redis hash的key值
      "redis_key": "sn",
      // 点击跳转的导航页
      "navigateTo": "../table/table"
    }
  ],
  "triggers": [
    {
      // excel表名
      "excel": "Trigger.xlsx",
      // sheet名
      "sheet": "ERegionTriggerType"
    },
    {
      // excel表名
      "excel": "Trigger.xlsx",
      // sheet名
      "sheet": "EItemUseScript"
    },
    {
      // excel表名
      "excel": "Trigger.xlsx",
      // sheet名
      "sheet": "EConditionType"
    },
    {
      // excel表名
      "excel": "Trigger.xlsx",
      // sheet名
      "sheet": "EnumQuestGoalType"
    },
    {
      // excel表名
      "excel": "Trigger.xlsx",
      // sheet名
      "sheet": "EActionType"
    }
  ],
  // 任务类型
  "EnumQuestType": ["1", "主线任务", "2", "结局任务", "3", "关系任务", "4", "奇遇任务"],
  // 任务页签
  "QuestTab": [{
				key: "questProp", 
				value: "任务属性"
		},{
				key: "questAcpt", 
				value: "任务接取",
		},{
				key: "questGoal", 
				value: "任务目标",
		},{
				key: "questComp", 
				value: "任务完成",
		}],
  // 任务目标关系类型
  "RelationType": ["单个", "与关系", "或关系"],
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
	},
	GetRedisTableNames() {
		var redis_table_names = []
		for (var i = 0; i < ExcelConfig_json['configs'].length; i++) {
			redis_table_names.push(ExcelConfig_json['configs'][i]['redis_table'])
		}
		return redis_table_names
	}
}
