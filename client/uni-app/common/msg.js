// 服务器地址
var SERVER_URL = 'http://172.17.145.203:28100';
// 操作成功代码
const RESULT_OK = "1";
// 操作失败代码
const RESULT_FAILED = "0";

export default {
	RESULT_OK,
	RESULT_FAILED,
	url() {
		return SERVER_URL;
	},
	// 登陆
	login(name, pwd) {
		return {
			'cmd': 0,
			'name': name,
			'pwd': pwd
		};
	},
	login_token(token) {
		return {
			'cmd': 0,
			'token': token
		};
	},
	test(token) {
		return {
			'cmd':1,
			'token':token
		};
	},
	get_table_data(token, table_name) {
		return {
			'cmd': 2,
			'token': token,
			'table_name': table_name
		};
	},
	get_all_quest_brief(token) {
		return {
			'cmd': 3,
			'token': token
		};
	},
	update_table_data_from_svn(token) {
		return {
			'cmd': 4,
			'token': token
		};
	},
	get_table_data_by_sn(token, table, sn) {
		return {
			'cmd': 5,
			'token': token,
			'table': table,
			'sn': sn
		};
	},
	// 更新数据表数据
	update_table_data(token, table, sn, field, value, verNum) {
		return {
			'cmd': 6,
			'token': token,
			'table': table,
			'sn': sn,
			'field': field,
			'value': value,
			'verNum': verNum
		};
	},
	// 新增表格数据
	add_table_data(token, table, keyValues) {
		return {
			'cmd': 7,
			'token': token,
			'table': table,
			'kv': keyValues
		};
	},
	// 任务目标数据
	get_quest_goal_info(token) {
		return {
			'cmd': 8,
			'token': token
		};
	},
	download_table(token, excelName, sheetName) {
		return {
			'cmd': 9,
			'token': token,
			'excel_name': excelName,
			'sheet_name': sheetName
		};
	},
	get_table_status(token, redisTableNames) {
		return {
			'cmd': 10,
			'token': token,
			'table_names': redisTableNames
		}
	},
	submit_to_svn(token) {
		return {
			'cmd': 11,
			'token': token,
		}
	},
	get_condition_info(token) {
		return {
			'cmd': 12,
			'token': token,
		}
	},
	// 更新任务表数据
	update_quest_data(token, sn, field, value, verNum) {
		return update_table_data(token, 'QUEST', sn, field, value, verNum);
	},
}
