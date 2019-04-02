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
	test(token){
		return{
			'cmd':1,
			'token':token
		};
	},
	get_table_data(token, table_name){
		return{
			'cmd': 2,
			'token': token,
			'table_name': table_name
		};
	}
}
