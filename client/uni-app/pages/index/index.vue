<template>
	<view class="content">
		<view class="input-group">
			<form @submit="formSubmit" @reset="formReset">
				<el-row class="login-fields">
					<el-row class="login-input-hint">SVN用户名：</el-row>
					<el-row>
						<input :span="6" class="login-input-field" type="text" clearable focus name="input-name" placeholder="请输入SVN账号" value="zhangzeqiang"/>
					</el-row>
				</el-row>
				<el-row class='login-fields'>
					<el-row class="login-input-hint">SVN密码：</el-row>
					<el-row >
						<input :span="6" class="uni-input login-input-field" type="password" name="input-pwd" placeholder="请输入SVN密码" value="GNT8EYkz"/>
					</el-row>
				</el-row>
				<view class="btn-row">
					<button type="primary" class="primary login-page-btn" formType="submit">登陆</button>
					<button type="default" class="login-page-btn" formType="reset">重置</button>
				</view>
			</form>
		</view>
	</view>
</template>

<script>
	import utils from '../../common/util.js'
	import msg from '../../common/msg.js'
	export default {
		data() {
			return {
				
			}
		},
		onLoad() {
			var _this = this
			// 获取缓存信息
			uni.getStorage({
				key: 'token',
				success(store) {
					var token = store.data
					// 使用token登陆
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.login_token(token),
						success: res => {
							// 登陆成功
							if (res.data['ret']) {
								_this.onLogin(token)
							}
						}
					});
				}
			})
		},
		methods: {
			// 登陆
			formSubmit(e) {
				var name = e.detail.value['input-name']
				var pwd = e.detail.value['input-pwd']
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.login(name, pwd),
					success: res => {
						// 登陆成功
						if (res.data['ret']) {
							var token = res.data['token']
							this.onLogin(token)
						} else {
							uni.showModal({
								title: '错误',
								content: '登陆失败！'
							})
						}
					},
					fail: () => {},
					complete: () => {}
				});
			},
			formReset(e) {
				console.log('清空数据')
			},
			onLogin(token) {
				uni.setStorageSync('token', token)
				this.$store.state.hasLogin = true
				this.$store.state.token = token
				// 跳转home界面
				uni.navigateTo({
					url: '../home/home',
					success: res => {},
					fail: () => {},
					complete: () => {}
				});
			}
		}
	}
</script>

<style>
	.uni-form-item .title {
		padding: 5upx 0;
	}
	
	.login-input-field {
		margin-left: 30upx;
		margin-right: 30upx;
		border-style: solid;
		border-width: 2upx;
		padding: 8upx;
	}
	
	.login-input-hint {
		margin-left: 30upx;
		padding: 5upx;
	}
	
	.login-fields {
		margin-left: 30upx;
		margin-right: 30upx;
		padding-bottom: 10upx;
	}
	
	.login-fields:first-child {
		padding-top: 30upx;
		margin-left: 30upx;
		margin-right: 30upx;
	}
	
	.login-page-btn {
		margin-top: 20upx;
		width: 35%;
		font-size: 30upx;
	}
</style>
