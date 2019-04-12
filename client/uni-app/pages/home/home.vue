<template>
	<view class="dialog">
		<view>
			<view style="margin-bottom: 10px">
				<el-row>
					<el-col :span="8">
						<el-button type="primary" round @click="refreshTableData">更新服务器表格数据</el-button>
					</el-col>
			
					<el-col :span="12">
						<el-input placeholder="搜索表格" v-model="filters[0].value"></el-input>
					</el-col>
					
					<el-col :span="4">
						<el-button type="primary" icon="el-icon-share" @click="onAbout"></el-button>
					</el-col>
				</el-row>
			</view>
			
			<data-tables :data="tables" layout="table" :action-col="actionCol" :filters="filters" :table-props="tableProps" 
						style="margin-left: 20upx; margin-right: 20upx" @selection-change="onSelectionChange"  class="mainContent">
				<!-- <el-table-column type="selection" width="55"></el-table-column> -->
				<el-table-column v-for="title in titles" :prop="title.prop" :label="title.label" :key="title.prop" sortable="custom"></el-table-column>
			</data-tables>
			
			<vue-canvas-nest :config="{color:'255,0,0', opacity:'0.8'}"></vue-canvas-nest>
		</view>
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
					prop: ['excel', 'sheet'],
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
				uni.navigateTo({
						url: t_config['navigateTo']+"?table_name="+t_config['redis_table'],
						success: res => {},
						fail: () => {},
						complete: () => {}
				})
			},
			onSelectionChange: function(rows) {
				this.selectedRows = rows;
				console.log(this.selectedRows.length);
			},
			onAbout: function() {
				uni.navigateTo({
						url: "../about/about",
						success: res => {},
						fail: () => {},
						complete: () => {}
				})
			},
			refreshTableData: function (tableName) {
				const loading = this.$loading({
					lock: true,
					text: '更新中...',
					spinner: 'el-icon-loading',
					background: 'rgba(0, 0, 0, 0.7)'
				})
				
				setTimeout(() => {
					loading.close()
				}, 2000)
				
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.update_table_data_from_svn(this.$store.state.token),
					success: res => {
						var result = res.data['result'];
						loading.close()
						if (result == 1) {
							this.$message({
								message: '更新成功！',
								type: 'success'
							})
						}
						else {
							this.$alert('原因：' + res.data['desc'], '更新失败', {
								confirmButtonText: '确认',
							});
						}
					},
					fail: res => {
						loading.close()
						this.$alert('操作超时，更新失败！', '更新失败', {
							confirmButtonText: '确认',
						});
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
	
	.el-input {
		width: 45%;
	}
</style>
