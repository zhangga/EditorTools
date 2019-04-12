<template>
	<view>
		<!-- 任务目标 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务目标</span>
			</view>
			<el-form label-width="30upx">
				<el-form-item label="任务名称">
				</el-form-item>
			</el-form>
		</el-card>
		
		<!-- 用于触发数据同步与更新 -->
		<span style="display:none"> {{tableRowData['questName']}} </span>
	</view>
</template>

<script>
	import config from '../../common/config.js'
	
	export default {
		data() {
			return {
				/* 全局相关 */
				hasSetDefaultValue: false,
				prevTableRowData: null,
				canAutomaticDeliver: false,
				goalNull: true
			};
		},
		props: ['tableRowData'],
		mounted: function() {
			this.refreshDefaultValues()
		},
		updated: function() {	
			// 如果是用户输入触发的更新，不刷新默认值
			if (this.hasSetDefaultValue) {
				// 如果是由于切换任务导致的更新，刷新默认值
				if (this.prevTableRowData != this.tableRowData) {
					this.refreshDefaultValues()
					this.prevTableRowData = this.tableRowData
				}
			} else {
				// 如果还未设置过默认值，执行设置
				this.refreshDefaultValues()
				this.hasSetDefaultValue = true;
				this.prevTableRowData = this.tableRowData;
			}
		},
		methods: {
			refreshDefaultValues: function() {
			},
			onGoalNullChange: function(e) {
				console.log(e)
			}
		}
	}
</script>

<style>
  .box-card {
  	  width: 470upx;
  	  margin-bottom: 10upx;
  }
  .el-form {
  	align: middle;
  }
  .el-form-item {
  	height: 20upx;
  }
  .text {
  	  font-size: 6upx;
  	  font-family: "PingFang SC"
  }
  .el-input {
  	  font-size: 6upx;
  	  font-family: "PingFang SC"
  }
  .clearfix {
  	  font-size: 10upx;
  	  font-weight: bold
  }
</style>
