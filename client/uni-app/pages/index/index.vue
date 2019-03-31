<template>
	<view class="content">
		<view>
			<button type="primary" @click="openXlsx">打开文件</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				news: []
			}
		},
		onLoad() {
			uni.showLoading({
				title: '加载中......',
				mask: false
			});
			uni.request({
				url: 'https://unidemo.dcloud.net.cn/api/news',
				method: 'GET',
				data: {},
				success: res => {
					console.log(res);
					this.news = res.data;
					uni.hideLoading();
				},
				fail: () => {},
				complete: () => {}
			});
		},
		methods: {
			// 打开xlsx
			openXlsx(e) {
				uni.downloadFile({
					url: 'D:\\SandBox\\trunk\\public\\config\\Action.xlsx',
					success(res) {
						var filePath = res.tempFilePath;
						uni.openDocument({
							filePath: filePath,
							success: function(res) {
								console.log('打开文档成功');
							}
						})
					}
				});
			}
		}
	}
</script>

<style>
	.uni-media-list-body {
		height: auto;
	}
	.uni-media-list-text-to {
		line-height: 1.6em;
	}
</style>
