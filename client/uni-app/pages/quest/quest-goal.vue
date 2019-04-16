<template>
	<view style="flex-direction: column;">
		<!-- 任务目标 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务目标</span>
			</view>
			<el-form label-width="60upx">
				<el-form-item label="目标ID:">
					<textInput :datas="questGoalSearch" placeholder='任务目标' :method='loadTableGoal' :select="setQuestGoal" v-bind:value="goal">
					</textInput>
				</el-form-item>
				<el-form-item label="策划备注: ">
					<span class="goal-desc">{{this.goalDesc}}</span>
				</el-form-item>
			</el-form>
			
			<el-popover placement="right-start" width="800" trigger="click" v-model="isEditorGoal">
				<el-button slot="reference" type='success' round class="float" @click="onEditorBtn">编辑目标</el-button>
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">编辑目标========><span style="color:#F00">{{goal}}</span></span>
					</view>
					<el-form label-width="60upx">
						<el-form-item label="组合类型">
							<el-select v-model="combinationType" size="medium" id="combinationType" @change="onEditorBtn">
								<el-option v-for="index in ConfigCombinType.length" :key="index" :value="index-1" 
									:label="ConfigCombinType[index-1]"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="关系类型" label-width="200upx" v-if="combinationType==1">
							<el-select v-model="relationType" size="medium" id="relationType" @change="onEditorBtn">
								<el-option v-for="index in ConfigRelationType.length" :key="index" :value="index-1" 
									:label="ConfigRelationType[index-1]"></el-option>
							</el-select>
						</el-form-item>
						<el-card class="box-card" v-for="goalIndex in combinGoal.length">
							<el-form label-width="60upx">
								<el-form-item label="关系类型">
									<el-select v-model="combinGoal[goalIndex].relationType" size="medium" @change="onEditorBtn">
										<el-option v-for="index in ConfigRelationType.length" :key="index" :value="index-1" 
											:label="ConfigRelationType[index-1]"></el-option>
									</el-select>
								</el-form-item>
							</el-form>
						</el-card>
					</el-form>
				</el-card>
			</el-popover>
			
			<el-popover placement="left-start" width="800" trigger="click" v-model="isAddGoal">
				<el-button slot="reference" type='warning' style="float: right" @click="onEditorBtn">新增目标</el-button>
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">新增目标</span>
					</view>
					<el-form label-width="60upx">
					</el-form>
				</el-card>
			</el-popover>
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
				goal: '',
				goalInfo: '',
				goalDesc: '',
				combinationType: 0,
				relationType: 0,
				combinGoal: [],
				combinRelationType: [],
				/** 编辑任务目标 */
				isEditorGoal: false,
				isAddGoal: false,
				/** 关系类型 */
				ConfigCombinType: [],
				ConfigRelationType: [],
			};
		},
		props: ['tableRowData'],
		mounted: function() {
			this.ConfigRelationType = config.ExcelConfig()['RelationType']
			this.ConfigCombinType = config.ExcelConfig()['CombinType']
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
			// 更新任务目标
			updateGoalSn: function(sn) {
				this.goal = sn
				this.goalInfo = this.questGoalTable.get(this.goal)
				if (this.goalInfo == null) {
					this.goal = ''
					this.goalDesc = '没有任务目标！'
					this.combinationType = 0
					this.relationType = 0
					this.combinationGoal = []
				} else {
					this.goalDesc = this.goalInfo.desc
					this.relationType = this.ConfigRelationType[this.goalInfo.relationType]
					this.combinationType = this.ConfigCombinType[this.goalInfo.combinationType-1]
					this.parseCombinGoal()
				}
			},
			parseCombinGoal: function() {
				/** 组合目标 */
				if (this.goalInfo.combinationType == 2) {
					for (var i = 0; i < this.goalInfo.condList.length; i++) {
						this.parseBaseGoal(this.goalInfo.condList[i], i)
					}
				} else {
					this.parseBaseGoal(this.goal, 0)
				}
			},
			// 解析基础目标
			parseBaseGoal: function(sn, index) {
				let data = this.questGoalTable.get(sn)
				this.combinGoal[index] = data
				this.combinRelationType[index] = data.relationType
				console.log(this.combinRelationType.data[index])
			},
			// 设置任务目标
			setQuestGoal: function (item) {
				console.log("设置任务目标")
				// let sn = item.value.substring(0, item.value.lastIndexOf(':'))
				// this.updateGoalSn(sn)
			},
			onGoalNullChange: function(e) {
				console.log(e)
			},
			onEditorBtn: function(e) {
				
			}
		}
	}
</script>

<style>
  view {
  	//border: #000000 solid 1upx;
  	font-size: 15upx;
  	margin: 2upx;
  
  	display: flex;
  	flex-direction: row;
  }
  
  .el-form {
  	align: middle;
  }
  
  .el-form-item {
  	height: 20upx;
  }
  
  .goal-desc {
	font-size: 18px;
  }
</style>
