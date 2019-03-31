// 服务器地址
var SERVER_URL = 'http://172.17.145.174:28100';

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
}
