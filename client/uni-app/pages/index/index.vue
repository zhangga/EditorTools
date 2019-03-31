<template>
	<view class="content">
		<view class="uni-padding-wrap uni-common-mt">
			<form @submit="formSubmit" @reset="formReset">
				<view class="uni-form-item uni-column">
					<view class="title">SVN用户名：</view>
					<input class="uni-input" name="input-name" placeholder="在这输入姓名" value="zhangzeqiang" />
					<view class="title">SVN密码：</view>
					<input class="uni-input" name="input-pwd" placeholder="在这输入密码" value="GNT8EYkz" />
				</view>
				<view class="uni-btn-v">
					<button formType="submit">登陆</button>
					<button type="default" formType="reset">重置</button>
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
				success(res) {
					var token = res.data['token']
					// 使用token登陆
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.login_token(token),
						success: res => {
							// 登陆成功
							if (res.data['ret']) {
								this.onLogin(token)
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
</style>
