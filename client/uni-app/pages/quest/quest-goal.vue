<template>
	<view>
		<!-- 任务目标 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务目标</span>
			</view>
			<el-form label-width="50upx">
				<el-form-item label="目标ID: ">
					<textInput :datas="questGoalSearch" placeholder='任务目标' :method='loadTableGoal' :select="setQuestGoal" v-bind:content="goal">
					</textInput>
				</el-form-item>
				<el-form-item label="策划备注: ">
					<el-input placeholder="请输入任务目标备注" v-model="this.goalInfo.desc">
					</el-input>
				</el-form-item>
			</el-form>
		</el-card>
		
		<!-- 用于触发数据同步与更新 -->
		<span style="display:none"> {{tableRowData['questName']}} </span>
	</view>
</template>

<script>
	import textInput from '../component/textInput.vue'
	import config from '../../common/config.js'
	import msg from '../../common/msg.js'
	
	export default {
		data() {
			return {
				/* 全局相关 */
				hasSetDefaultValue: false,
				prevTableRowData: null,
				/** 任务目标表数据 */
				questGoalTable: new Map(),
				questGoalSearch: [],
				goal: "",
				goalInfo: {}
			};
		},
		props: ['tableRowData'],
		mounted: function() {
			uni.request({
				url: msg.url(),
				method: 'GET',
				data: msg.get_table_data(this.$store.state.token, "QUESTGOAL"),
				success: res => {
					var items = res.data['data']
					for (let i = 0; i < items.length; i++) {
						var item = JSON.parse(items[i])
						this.questGoalTable.set(item.sn, item)
					}
					console.log('读取任务目标数据表完成')
					this.refreshDefaultValues()
				},
				fail: () => {},
				complete: () => {}
			});
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
		components: {
			textInput
		},
		methods: {
			refreshDefaultValues: function() {
				this.updateGoalSn(this.tableRowData['goal'])
			},
			// 读取QuestGoal数据表
			loadTableGoal: function () {
				var _search = this.questGoalSearch
				if (_search.length === 0) {
					var i = 0
					this.questGoalTable.forEach(function(v, key, map) {
						_search[i++] = {
							value: v.sn + ':' + v.desc
						}
					})
				}
			},
			// 设置任务目标
			setQuestGoal: function (item) {
				// let sn = item.value.substring(0, item.value.lastIndexOf(':'))
				// this.updateGoalSn(sn)
			},
			onGoalNullChange: function(e) {
				console.log(e)
			},
			updateGoalSn: function(sn) {
				this.goal = sn
				this.goalInfo = this.questGoalTable.get(this.goal)
				if (this.goalInfo == null) {
					this.goal = ''
					this.goalInfo = {desc: ''}
				}
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
	padding: 5upx;
  }
  .text {
  	  font-size: 8upx;
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
