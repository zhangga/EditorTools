import Vue from 'vue'
import msg from '../common/msg.js'
import store from '../store/index.js'

function updateDataField(table, sn, field, value) {
	uni.request({
		url: msg.url(),
		method: 'GET',
		data: msg.update_table_data(store.state.token, table, sn, field, value),
		success: res => {
// 			Vue.prototype.$message({
// 				message: '这是一条成功消息',
// 				type: 'success'
// 			});
		},
		fail: () => {
			Vue.prototype.$message.error(table+'表'+sn+'行'+field+'字段保存失败')
		},
		complete: () => {}
	});
}

function addDataField(table, keyValues, loadingInstance, caller) {
	uni.request({
		url: msg.url(),
		method: 'GET',
		data: msg.add_table_data(store.state.token, table, keyValues),
		success: res => {
			var resultCode = res.data['result']
			var hint = res.data['hint']
			var replyData = res.data['data']
			
			if (resultCode == msg.RESULT_OK) {
				if (loadingInstance != null) {
					loadingInstance.close()
				}
				
				Vue.prototype.$message.success('新增任务记录成功')
				
				if (caller != null) {
					caller.onAddTableData(replyData)
				}
			}
			else {
				Vue.prototype.$message.error('向' + table + '表新增记录失败，【原因】：' + hint)
			}
		},
		fail: () => {
			Vue.prototype.$message.error('向' + table + '表新增数据失败')
		},
		complete: () => { {
			if (loadingInstance != null) {
				loadingInstance.close()
			}
		}}
	});
}

function formatTime(time) {
	if (typeof time !== 'number' || time < 0) {
		return time
	}

	var hour = parseInt(time / 3600)
	time = time % 3600
	var minute = parseInt(time / 60)
	time = time % 60
	var second = time

	return ([hour, minute, second]).map(function (n) {
		n = n.toString()
		return n[1] ? n : '0' + n
	}).join(':')
}

function formatLocation(longitude, latitude) {
	if (typeof longitude === 'string' && typeof latitude === 'string') {
		longitude = parseFloat(longitude)
		latitude = parseFloat(latitude)
	}

	longitude = longitude.toFixed(2)
	latitude = latitude.toFixed(2)

	return {
		longitude: longitude.toString().split('.'),
		latitude: latitude.toString().split('.')
	}
}

var dateUtils = {
	UNITS: {
		'年': 31557600000,
		'月': 2629800000,
		'天': 86400000,
		'小时': 3600000,
		'分钟': 60000,
		'秒': 1000
	},
	humanize: function (milliseconds) {
		var humanize = '';
		for (var key in this.UNITS) {
			if (milliseconds >= this.UNITS[key]) {
				humanize = Math.floor(milliseconds / this.UNITS[key]) + key + '前';
				break;
			}
		}
		return humanize || '刚刚';
	},
	format: function (dateStr) {
		var date = this.parse(dateStr)
		var diff = Date.now() - date.getTime();
		if (diff < this.UNITS['天']) {
			return this.humanize(diff);
		}
		var _format = function (number) {
			return (number < 10 ? ('0' + number) : number);
		};
		return date.getFullYear() + '/' + _format(date.getMonth() + 1) + '/' + _format(date.getDay()) + '-' +
			_format(date.getHours()) + ':' + _format(date.getMinutes());
	},
	parse: function (str) { //将"yyyy-mm-dd HH:MM:ss"格式的字符串，转化为一个Date对象
		var a = str.split(/[^0-9]/);
		return new Date(a[0], a[1] - 1, a[2], a[3], a[4], a[5]);
	}
};

module.exports = {
	formatTime: formatTime,
	formatLocation: formatLocation,
	dateUtils: dateUtils,
	updateDataField: updateDataField,
	addDataField: addDataField,
}