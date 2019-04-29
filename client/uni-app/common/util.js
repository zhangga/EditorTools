import Vue from 'vue'
import msg from '../common/msg.js'
import store from '../store/index.js'

/* 本地变量 */
var tableDataCache = new Map()
var tableDataRequestLock = new Map()
var callbackCache = new Map()
var isQuestDataExpired = false

/* 获取并更新当前的用户token */
function getCurrentUserToken() {
	if (store.state.token != '') {
		return store.state.token
	}
	
	try {
		const token = uni.getStorageSync('token')
		if (token) {
			store.state.token = token
			return token;
		} else {
			this.$message({
				message: '获取用户Token失败，请重新登录！',
				type: 'error'
			})
		}
	} catch (e) {
		this.$message({
			message: '获取用户Token失败，请重新登录！',
			type: 'error'
		})
	}
}

function updateDataFieldNV(table, sn, field, value, caller) {
	updateDataField(table, sn, field, value, getCurrentUserToken(), caller)
}

/* 更改/更新表数据 */
function updateDataField(table, sn, field, value, verNum, caller) {
	uni.request({
		url: msg.url(),
		method: 'GET',
		data: msg.update_table_data(getCurrentUserToken(), table, sn, field, value, verNum),
		success: res => {
			var resultCode = res.data['result']
			var hint = res.data['hint']
			
			if (resultCode == msg.RESULT_FAILED) {
				Vue.prototype.$message.error('更新失败！' + hint)
			}
			else if (resultCode == msg.RESULT_OK) {
				// 更新当前缓存版本号
				caller.$store.state.verNum = res.data['data']
			}
		},
		fail: () => {
			Vue.prototype.$message.error(table + '表' + sn + '行' + field + '字段保存失败')
		},
		complete: () => {}
	});
}

/* 添加表数据 */
function addDataField(table, keyValues, loadingInstance, caller) {
	uni.request({
		url: msg.url(),
		method: 'GET',
		data: msg.add_table_data(getCurrentUserToken(), table, keyValues),
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

/** 
 * 加载表格数据
 * @param callerDataset: 调用方法的实例中，用于存储加载的表格数据的本地变量引用
 * @param tableName: 要加载的表格名
 * @param dataKeyName: 表格数据中，用于索引数据的列名（例如sn）
 * @param dataValueName: 表格数据中，用于描述数据的列名（例如name）
 * @param callback: 加载表格数据后的回调方法（可选）
 * @param caller: 方法调用者的实例（可选，但提供callback时一定要提供caller）
 */
function loadTableData(callerDataset, tableName, dataKeyName, dataValueName, callback, caller) {
	// 参数数量合法性判断
	if (arguments.length > 4 && arguments.length < 6) {
		console.log("方法参数不足！")
		return null
	}
	
	let tableData = tableDataCache.get(tableName)
	
	// 用于判断是否已经向服务器请求了（但尚未返回）当前表格的数据（以避免在结果返回前重复请求）
	let tableDataRequestFlag = tableDataRequestLock.get(tableName)
	
	// 注册回调方法
	if (callbackCache.get(tableName) == null) {
		callbackCache.set(tableName, [])
	}
	callbackCache.get(tableName).push({'caller': caller, 'callback': callback, 'callerDataset': callerDataset})
	
	// 当对同一个表格数据有重复的请求时，不再重复请求，但会缓存请求的回调方法(在callbackCache中)，在获取数据之后调用所有的回调方法
	if (tableDataRequestFlag) {
		return tableData
	}
	
	if (typeof tableData == 'undefined' || tableData.length === 0) {
		// 更新请求正在进行的状态
		tableDataRequestLock.set(tableName, true)
		
		tableData = []
		
		console.log("请求" + tableName + "表格数据")
		
		uni.request({
			url: msg.url(),
			method: 'GET',
			data: msg.get_table_data(getCurrentUserToken(), tableName),
			success: res => {
				console.log("获取了" + tableName + "表格数据")
				var items = res.data['data']
				for (let i = 0; i < items.length; i++) {
					var item = JSON.parse(items[i])
					tableData[i] = {
						key: item[dataKeyName],
						value: item[dataKeyName] + ':' + item[dataValueName]
					};
				}
				
				// 缓存到本地
				tableDataCache.set(tableName, tableData)

				// 逐个调用所有注册的回调方法
				let callbacks = callbackCache.get(tableName)
				
				for (let i = 0; i < callbacks.length; i++) {
					// 更新调用方本地缓存
					let currCallerDataset = callbacks[i]['callerDataset']
					currCallerDataset.length = 0
					currCallerDataset.push(...tableData)
					
					// 执行回调方法
					let currCaller = callbacks[i]['caller']
					let currCallback = callbacks[i]['callback']
					
					if (currCaller != null && currCallback != null) {
						console.log("为" + tableName + "执行回调")
						console.log(currCaller)
						currCallback.call(currCaller)
					}
				}
				
			},
			fail: () => {
				// TODO: Error Handling
			},
			complete: () => {
				tableDataRequestLock.set(tableName, false)
				return tableData
			}
		});
	} else {
		console.log("表格" + tableName + "数据已经存在")
		callerDataset.length = 0
		callerDataset.push(...tableData)
		
		if (caller != null && callback != null) {
			console.log("为" + tableName + "执行回调")
			callback.call(caller)
		}
		
		return tableData
	}
}

/* 功能性方法 */
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
	updateDataFieldNV: updateDataFieldNV,
	addDataField: addDataField,
	getCurrentUserToken: getCurrentUserToken,
	loadTableData: loadTableData,
	isQuestDataExpired: isQuestDataExpired
}