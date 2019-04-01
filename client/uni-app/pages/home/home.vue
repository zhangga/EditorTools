<template>
	<view class="content">
		<view class="uni-list">
			<view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(item,index) in news" :key="index">
				<view class="uni-media-list">
					{{item}}
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import utils from '../../common/util.js'
	import msg from '../../common/msg.js'
	export default {
		data() {
			return {
				news:[]
			};
		},
		onLoad() {
			uni.request({
				url: msg.url(),
				method: 'GET',
				data: msg.test(this.$store.state.token),
				success: res => {
					console.log(res.data['token'])
					this.news = res.data['test']
				},
				fail: () => {
					console.log(this.$store.state.token)
				},
				complete: () => {}
			});
		}
	}
</script>

<style>

</style>
