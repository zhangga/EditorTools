<template>
	<view class="dialog">
		<view class="loginPage">
			<h1 style="font-family: 'PingFang SC'; font-size: 14upx; text-align: center; margin-bottom: 2upx">登录</h1>
			<el-form @submit="formSubmit" @reset="formReset">
				<el-form-item label="SVN用户名">
					<el-input type="text" id="user" v-model="userForm.userName" @blur="onInputBlur" autofocus></el-input>
					<p>{{userNameErrorInfo}}</p>
				</el-form-item>
				<el-form-item label="密码">
					<el-input type="password" id="password" v-model="userForm.password" @blur="onInputBlur"></el-input>
					<p>{{passwordErrorInfo}}</p>
				</el-form-item>
				
				<view class="buttonGroup">
					<el-button type="primary" @click="formSubmit" v-bind:disabled="!isFormValid">提交</el-button>
					<el-button @click="formReset">重置</el-button>
				</view>
			</el-form>
		</view>
	</view>
</template>

<script>
	import utils from '../../common/util.js'
	import msg from '../../common/msg.js'
	export default {	
		data() {
			return {
				userForm: {
					userName: '',
					password: ''
				},
				isFormValid: false,
				userNameErrorInfo: '',
				passwordErrorInfo: ''
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
		mounted: function() {
			this.setDefaultValues()
		},
		methods: {
			setDefaultValues: function() {
				this.userForm.userName = 'zhangzeqiang'
				this.userForm.password = 'GNT8EYkz'
				this.isFormValid = true
			},
			onInputBlur: function() {
				if (this.userForm.userName == '') {
					this.userNameErrorInfo = '用户名不能为空'
				} else {
					this.userNameErrorInfo = ''
				}
				
				if (this.userForm.password == '') {
					this.passwordErrorInfo = '密码不能为空'
				} else {
					this.passwordErrorInfo = ''
				}
				
				if (this.userForm.userName != '' && this.userForm.password != '') {
					this.isFormValid = true
				} else {
					this.isFormValid = false
				}
			},
			// 登陆
			formSubmit: function() {
				var name = this.userForm.userName
				var pwd = this.userForm.password
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.login(name, pwd),
					success: res => {
						// 登陆成功
						// TODO: Notification
						if (res.data['ret']) {
							var token = res.data['token']
							this.onLogin(token)
						} else {
							// TODO: NOtification
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
			formReset: function() {
				this.setDefaultValues()
			},
			onLogin: function(token) {
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
	.dialog {
        width: 100%;
        height: 93vh;
        background: rgba(0,0,0,.8);
    }
    .loginPage {
        position: absolute;
        top: 35%;
        left: 50%;
        margin-top: -150px;
        margin-left: -175px;
        width: 350px;
        min-height: 300px;
        padding: 30px 20px 20px;
        border-radius: 8px;
        box-sizing: border-box;
        background-color: #fff;
    }
    .loginPage p {
        color: red;
        text-align: left;
		font-size: 5upx;
		height: auto;
    }
	.el-form-item:first-child {
		margin-bottom: 0upx;
	}
	.el-form-item:nth-child(2) {
		margin-bottom: 5upx;
	}
	.buttonGroup {
		position: relative;
		top: 50%;
		left: 50%;
	}
</style>
