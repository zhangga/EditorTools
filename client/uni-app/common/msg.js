// 服务器地址
var SERVER_URL = 'http://127.0.0.1:28100';

export default {
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
	// 更新任务表数据
	update_quest_data(token, sn, field, value) {
		return update_table_data(token, 'QUEST', sn, field, value);
	},
	// 更新数据表数据
	update_table_data(token, table, sn, field, value) {
		return {
			'cmd': 5,
			'token': token,
			'table': table,
			'sn': sn,
			'field': field,
			'value': value,
		};
	},
}
