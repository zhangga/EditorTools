<template>
	<view>
		<view class="uni-list">
			<view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(item,index) in table_data" :key="index">
				<view class="uni-media-list">
					<image class="uni-media-list-logo" :src="item.author_avatar"></image>
					<view class="uni-media-list-body">
						<view class="uni-media-list-text-top">{{item['sn']}}</view>
						<view class="uni-media-list-text-bottom uni-ellipsis">{{item['questName']}}</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import utils from '../../common/util.js'
	import msg from '../../common/msg.js'
	import config from '../../common/config.js'
	export default {
		data() {
			return {
				table_name: '',
				table_data: []
			};
		},
		onLoad(e) {
			this.table_name = e['table_name']
			uni.request({
				url: msg.url(),
				method: 'GET',
				data: msg.get_table_data(this.$store.state.token, this.table_name),
				success: res => {
					for (var i = 0; i < res.data['data'].length; i++) {
						this.table_data[i] = JSON.parse(res.data['data'][i])
					}
					console.log(this.table_data[0]['questName'])
				},
				fail: () => {
					console.log("失败！token = " + this.$store.state.token)
				},
				complete: () => {}
			});
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
