<template>
   <data-tables :data="table_data" :pagination-props="{ pageSizes: [5, 10, 15] }">
     <el-table-column v-for="title in table_title" :prop="title.prop" :label="title.label" :key="title.label">
     </el-table-column>
   </data-tables>
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
		onLoad(e) {
			this.table_name = e['table_name']
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
					
// 					this.table_data = [{
// 						"content": "Water flood",
// 						"flow_no": "FW201601010001",
// 						"flow_type": "Repair",
// 						"flow_type_code": "repair",
// 						}, {
// 						"content": "Lock broken",
// 						"flow_no": "FW201601010002",
// 						"flow_type": "Repair",
// 						"flow_type_code": "repair",
// 						}, {
// 						"content": "Help to buy some drinks",
// 						"flow_no": "FW201601010003",
// 						"flow_type": "Help",
// 						"flow_type_code": "help"
// 					}]
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
	.el-table .cell{
		white-space:pre-wrap;
	}
</style>
