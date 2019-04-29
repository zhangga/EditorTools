<template>
	<view class="dialog">
		<view class="loginPage">
			<h1 style="font-family: 'PingFang SC'; font-size: 14upx; text-align: center; margin-bottom: 2upx">登录</h1>
			<el-form :model="loginInfo" status-icon :rules="loginInfoRule" ref="loginForm">
				<el-form-item label="SVN用户名" prop="userName">
					<el-input type="text" id="user" v-model="loginInfo.userName" autofocus></el-input>
				</el-form-item>
				<el-form-item label="密码" prop="password">
					<el-input type="password" id="password" v-model="loginInfo.password"></el-input>
				</el-form-item>
				<el-form-item>
					<el-row>
						<el-col :span="9" :offset="15">
							<el-button type="primary" @click="formSubmit" >提交</el-button>
							<el-button @click="formReset">重置</el-button>
						</el-col>
					</el-row>
				</el-form-item>
			</el-form>
		</view>
		<vue-canvas-nest :config="{color:'255,0,0', opacity:'0.8'}"></vue-canvas-nest>
	</view>
</template>

<script>
	import util from '../../common/util.js'
	import msg from '../../common/msg.js'
	
	export default {	
		data() {
			return {
				loginInfo: {
					userName: '',
					password: ''
				},
				loginInfoRule: {
					userName: [{required: true, message: '用户名不能为空', trigger: 'blur'}],
					password: [{required: true, message: '密码不能为空', trigger: 'blur'}]
				}
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
			formSubmit: function() {
				this.$refs['loginForm'].validate((valid) => {
					if (valid) {
						uni.request({
							url: msg.url(),
							method: 'GET',
							data: msg.login(this.loginInfo.userName, this.loginInfo.password),
							success: res => {
								// 登陆成功
								if (res.data['ret']) {
									var token = res.data['token']
									this.onLogin(token)
								} else {
									this.$notify.error({
										title: '登陆失败！',
										message: '请检查输入的用户名和密码'
									});
								}
							},
							fail: () => {},
							complete: () => {}
						});
					} else {
						return false
					}
				})
			},
			formReset: function() {
				this.$refs['loginForm'].resetFields();
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
    .loginPage {
        position: absolute;
        top: 35%;
        left: 50%;
        margin-top: -70upx;
        margin-left: -90upx;
        width: 180upx;
        min-height: 150upx;
        padding: 10upx;
        border-radius: 8upx;
        box-sizing: border-box;
        background-color: #fff;
    }
    .loginPage p {
        color: red;
        text-align: left;
		font-size: 5upx;
		height: auto;
    }
	.el-form-item {
		margin-bottom: 6upx;
	}
	.el-form-item:last-child {
		margin-top: 15upx;
		margin-bottom: 3upx;
		padding: 0upx;
	}
	/* .buttonGroup {
		position: relative;
		top: 50%;
		left: 60%;
	} */
</style>
