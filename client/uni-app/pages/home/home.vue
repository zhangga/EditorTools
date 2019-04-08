<template>
	<view>
		<view style="margin-bottom: 10px">
			<el-row>
				<el-col :span="18">
					<el-button type="primary" round @click="refreshTableData">更新服务器表格数据</el-button>
				</el-col>

				<el-col :span="6">
					<el-input placeholder="搜索表格" v-model="filters[0].value"></el-input>
				</el-col>
			</el-row>
		</view>

	<data-tables :data="tables" layout="table" :action-col="actionCol" :filters="filters" :table-props="tableProps" 
				style="margin-left: 20upx; margin-right: 20upx" @selection-change="onSelectionChange">
		<el-table-column type="selection" width="55"></el-table-column>
		<el-table-column v-for="title in titles" :prop="title.prop" :label="title.label" :key="title.prop" sortable="custom"></el-table-column>
	</data-tables>
  </view>
</template>

<script>
	import config from '../../common/config.js'
	import msg from '../../common/msg.js'
	
	export default {
		data() {
			return {
				titles: [
					{
						prop: 'excel',
						label: 'Excel名'
					}, 
					{
						prop: 'sheet',
						label: '表格名'
					},
					{
						prop: 'Status',
						label: '状态'
					}],
				tables: [],
				filters: [{
					prop: 'excel',
					value: ''
				}],
				actionCol: {
					label: '操作',
					props: {
					  align: 'center'
					},
					buttons: [{
						props: {
							type: 'primary'
						},
						handler: (row) => {
							this.open_table(row.excel, row.sheet)
						},
						label: '编辑'
					}]
				},
				tableProps: {
					border: true,
					stripe: true,
					'cell-style': {
						'text-align': 'center'
					},
					'header-cell-style': {
						'text-align': 'center'
					}
				},
				selectedRows: []
			}
		},
		onLoad() {
			this.tables = config.ExcelConfig().configs
		},
		methods: {
			open_table: function (excelName, sheetName) {
				var t_config = config.GetExcelConfig(excelName, sheetName)
				console.log("打开表: " + t_config['excel'])
				//uni.navigateTo({
					//url: t_config['navigateTo'] + "?table_name=" + table_name,
					//url: "../tab/tab",
					//success: res => {},
					//fail: () => {},
					//complete: () => {}
				//});
	// 				uni.switchTab({
	// 						url: "../property/property",
	// 						success: res => {},
	// 						fail: () => {},
	// 						complete: () => {}
	// 				})
				uni.navigateTo({
						url: "../quest/quest",
						success: res => {},
						fail: () => {},
						complete: () => {}
				})
			},
			onSelectionChange: function(rows) {
				this.selectedRows = rows;
				console.log(this.selectedRows.length);
			},
			refreshTableData: function (tableName) {
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_all_quest_brief(this.$store.state.token),
					success: res => {
						this.items = res.data['data']
						// 排序
						for (let i = 0; i < this.items.length; i++) {
							let children = this.items[i]['children']
							children.sort(function(a, b) {
								return a['id'] - b['id']
							})
						}
					}
				});
			}
		}
	}
</script>

<style>
	.el-col {
		padding-left: 25upx;
	}
	
	.el-col:last-child {
		padding-right: 25upx;
	}
</style>
