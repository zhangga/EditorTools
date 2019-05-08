<template>
	<view class="dialog">
		<view>
			<!-- <data-tables :data="table_data" layout="table" style="margin-left: 20upx; margin-right: 20upx" class="mainContent">
				<el-table-column type="selection" width="55"></el-table-column>
				<el-table-column v-for="title in table_title" :prop="title.prop" :label="title.label" :key="title.label" sortable="custom"></el-table-column>
			</data-tables> -->
			
			<el-table :data="table_data" style="width: 93%" height="900" class="mainContent">
				<el-table-column v-for="title in table_title" :prop="title.prop" :label="title.label" :key="title.label" sortable="custom"></el-table-column>
			</el-table>
			
			<vue-canvas-nest :config="{color:'255,0,0', opacity:'0.8'}"></vue-canvas-nest>
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
				table_title: [],
				table_data: []
			};
		},
		onLoad(option) {
			// 接受页面跳转传递的参数（uni-app跳转传参）
			this.table_name = option.table_name
			uni.request({
				url: msg.url(),
				method: 'GET',
				data: msg.get_table_data(this.$store.state.token, this.table_name),
				success: res => {
					var datas = []
					for (var i = 0; i < res.data['data'].length; i++) {
						datas[i] = JSON.parse(res.data['data'][i])
					}
					this.table_data = datas
					
					var titles = []
					for (var i = 0; i < res.data['titles'].length; i++) {
						titles[i] = JSON.parse(res.data['titles'][i])
					}
					this.table_title = titles
					
					console.log(this.table_data)
					console.log(this.table_title)
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
	.el-table {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
	}
</style>
