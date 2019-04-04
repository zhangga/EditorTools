<template>
	<view class="content">
		<view class="uni-list">
			<view class="uni-list-cell" hover-class="uni-list-cell-hover" v-for="(item,index) in tables" :key="index"
			@tap="open_table" :data-table="item.redis_table">
				<view class="uni-media-list">
					{{item.sheet}}
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
				tables:[]
			};
		},
		onLoad() {
			console.log(config.GetExcelConfig('QUEST'))
			this.tables = config.ExcelConfig().configs
		},
		methods: {
			open_table(e) {
				var table_name = e.currentTarget.dataset.table
				var t_config = config.GetExcelConfig(table_name)
				console.log("打开表: " + t_config['excel'])
				uni.navigateTo({
					url: t_config['navigateTo'] + "?table_name=" + table_name,
					success: res => {},
					fail: () => {},
					complete: () => {}
				});
// 				uni.switchTab({
// 						url: "../property/property",
// 						success: res => {},
// 						fail: () => {},
// 						complete: () => {}
// 				})
			}
		}
	}
</script>

<style>

</style>
