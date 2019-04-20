<template>
	<view style="flex-direction: column">
		
		<!-- 任务目标 -->
		<el-card class="box-card">
			
			<el-button type='info' round class="float" @click="onClickAddQuestGoal">新增任务目标</el-button>
			<el-dialog title="新增任务目标" width="42%" :visible.sync="showAddGoal" center>
				<el-form label-width="60upx" style="width:85%">
					<el-form-item label="任务目标ID">
						<el-input v-model="newGoalId"></el-input>
					</el-form-item>
					<el-form-item label="策划备注">
						<el-input v-model="newGoalDesc"></el-input>
					</el-form-item>
				</el-form>
				<span slot="footer" class="dialog-footer">
					<el-button type="primary" @click="onAddQuestGoal">提交</el-button>
				</span>
			</el-dialog>
			
			<view slot="header" class="clearfix">
				<span class="header">任务目标</span>
			</view>
			<el-form label-width="40upx">
			<el-row>
				<el-col :span="8">
					<el-form-item label="目标ID:">
						<textInput :datas="questGoalSearch" placeholder='任务目标' :method='loadTableGoal' :select="setQuestGoal" v-bind:value="goalSn">
						</textInput>
					</el-form-item>
				</el-col>
				<el-col :offset="6" :span="8">
					<el-form-item label="备注">
						<el-input type="textarea" :rows="1" v-model="baseGoal.desc" placeholder="备注信息" @change="onGoalDescChange($event)"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
			</el-form>
			

			<el-form label-width="30upx">
				<el-card class="box-card" v-for="goalIndex in combinGoal.length" shadow="hover">
					<view slot="header">
						<el-row style="width: 100%">
							<el-col :span="8">
								<el-form-item label="组合类型">
									<el-select v-model="baseGoal.combinationType" size="medium" id="combinationType" @change="onSelectCombinationType">
										<el-option v-for="index in ConfigCombinType.length" :key="index" :value="index-1" 
											:label="ConfigCombinType[index-1]" :disabled="index>1"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="8" :offset="1">
								<el-form-item label="关系类型">
									<el-select v-model="combinGoal[goalIndex-1].relationType" size="medium" @change="onSelectGoalRelate($event, goalIndex-1)">
										<el-option v-for="index in ConfigRelationType.length" :key="index" :value="index-1" 
											:label="ConfigRelationType[index-1]"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="4" :offset="3">
								<el-form-item label-width="0upx">
									<el-button round type="success" icon="el-icon-circle-plus" @click="onAddGoal(goalIndex-1)">新增目标列</el-button>
								</el-form-item>
							</el-col>
						</el-row>
					</view>
					<el-form label-width="30upx">
						<el-container v-for="condIndex in combinGoal[goalIndex-1].condList.length">
							<el-main>
								<el-row>
									<el-col :span="9">
										<el-form-item label="目标类型">
											<el-autocomplete placeholder="请输入内容"
												v-model="combinGoal[goalIndex-1].condList[condIndex-1]" 
												:fetch-suggestions="queryEnumGoal"  @select="onSelectEnumGoal($event, goalIndex-1, condIndex-1)">
											</el-autocomplete>
										</el-form-item>
									</el-col>
									<el-col :span="9" :offset="2">
										<el-form-item label="参数">
											<el-input v-model="combinGoal[goalIndex-1].params[condIndex-1]" @change="onParamsChange($event, goalIndex-1, condIndex-1)" 
												placeholder="请输入参数"></el-input>
										</el-form-item>
									</el-col>
								</el-row>
								<el-row>
									<el-col :span="9">
										<el-form-item label="目标位置">
											<el-input v-model="combinGoal[goalIndex-1].targetPos[condIndex-1]" @change="onPosChange($event, goalIndex-1, condIndex-1)" 
												placeholder="请输入参数"></el-input>
										</el-form-item>
									</el-col>
									<el-col :span="9" :offset="2">
										<el-form-item label="参数说明">
											<el-input :disabled="true"
												v-model="enumGoalTable.get(combinGoal[goalIndex-1].condList[condIndex-1]).param"></el-input>
										</el-form-item>
									</el-col>
									<el-col :span="2" :offset="1">
										<el-form-item label-width="0upx" style="">
											<el-button type="danger" icon="el-icon-remove" @click="onRemoveGoal(goalIndex-1, condIndex-1)">删除</el-button>
										</el-form-item>
									</el-col>
								</el-row>
								
								<div class="hr anim"></div>
							</el-main>
						</el-container>
					</el-form>
				</el-card>
			</el-form>
			
			<el-row>
				<el-col :span="4">
					<el-button type='success' icon="el-icon-search" @click="showItem = true">查询物品</el-button>
					<el-dialog title="查询物品" width="42%" :visible.sync="showItem" center>
						<el-form label-width="120upx">
							<el-form-item label="物品信息">
								<textInput :datas="itemSearch" placeholder='物品查询' :method='loadItem' :select="onSelect" v-bind:value="searchText">
								</textInput>
							</el-form-item>
						</el-form>
						<span slot="footer" class="dialog-footer">
							<el-button type="primary" @click="showItem = false">完成</el-button>
						</span>
					</el-dialog>
				</el-col>
				<el-col :span="4" :offset="6">
					<el-button type='warning' icon="el-icon-search" @click="showNpc = true">查询NPC</el-button>
					<el-dialog title="NPC查询" width="42%" :visible.sync="showNpc" center>
						<el-form label-width="120upx">
							<el-form-item label="NPC信息">
								<textInput :datas="npcSearch" placeholder='NPC查询' :method='loadNpc' :select="onSelect" v-bind:value="searchText">
								</textInput>
							</el-form-item>
						</el-form>
						<span slot="footer" class="dialog-footer">
							<el-button type="primary" @click="showNpc = false">完成</el-button>
						</span>
					</el-dialog>
				</el-col>
				<el-col :span="4" :offset="6">
					<el-button type='danger' icon="el-icon-search" @click="showMonster = true">查询怪物</el-button>
						<el-dialog title="怪物查询" width="42%" :visible.sync="showMonster" center>
							<el-form label-width="120upx">
								<el-form-item label="怪物信息">
									<textInput :datas="monsterSearch" placeholder='怪物查询' :method='loadMonster' :select="onSelect" v-bind:value="searchText">
									</textInput>
								</el-form-item>
							</el-form>
							<span slot="footer" class="dialog-footer">
								<el-button type="primary" @click="showMonster = false">完成</el-button>
							</span>
						</el-dialog>
				</el-col>
			</el-row>
		</el-card>
		
		<!-- 用于触发数据同步与更新 -->
		<span style="display:none"> {{tableRowData['questName']}} </span>
	</view>
</template>

<script>
	import textInput from '../component/textInput.vue'
	import config from '../../common/config.js'
	import msg from '../../common/msg.js'
	import util from '../../common/util.js'
	
	export default {
		data() {
			return {
				/* 全局相关 */
				hasSetDefaultValue: false,
				prevTableRowData: null,
				searchText: '',
				itemSearch: [],
				npcSearch: [],
				monsterSearch: [],
				QUEST: 'QUEST',
				QUESTGOAL: 'QUESTGOAL',
				/* 任务目标表数据 */
				questGoalTable: new Map(),
				questGoalSearch: [],
				enumGoalTable: new Map(),
				enumGoalSearch: [],
				enumGoalDesc: new Map(),
				newGoalId: 0,
				newGoalDesc: '新任务目标',
				goalVerNum: '-1',
				/* 目标组 */
				goalSn: '',
				baseGoal: {combinationType: 0,
					relationType: 0,
					desc: '没有任务目标！'},
				combinGoal: [],
				/* 编辑任务目标 */
				showItem: false,
				showNpc: false,
				showMonster: false,
				showAddGoal: false,
				/* 关系类型 */
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
				data: msg.get_quest_goal_info(util.getCurrentUserToken()),
				success: res => {
					var items = res.data['data']
					for (let i = 0; i < items.length; i++) {
						let item = JSON.parse(items[i])
						this.questGoalTable.set(item.sn, item)
					}
					var enumGoal = res.data['enumGoal']
					for (let i = 0; i < enumGoal.length; i++) {
						let item = enumGoal[i]		
						this.enumGoalTable.set(item.sn.toString(), item)
						this.enumGoalDesc.set(item.sn, item.param)
						this.enumGoalSearch[i] = {value: item.sn + ':' + item.desc}
					}
					console.log('读取任务目标数据表完成')
					
					console.log(this.enumGoalTable)
					
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
			loadNpc: function () {
				if (this.npcSearch.length === 0) {
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(util.getCurrentUserToken(), "NPC"),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								this.npcSearch[i] = {
									value: item.sn + ':' + item.name
								};
							}
						}
					});
				}
			},
			loadItem: function () {
				if (this.itemSearch.length === 0) {
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(util.getCurrentUserToken(), "ITEM"),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								this.itemSearch[i] = {
									value: item.sn + ':' + item.name
								};
							}
						}
					});
				}
			},
			loadMonster: function () {
				if (this.monsterSearch.length === 0) {
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(util.getCurrentUserToken(), "CHARACTER"),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								this.monsterSearch[i] = {
									value: item.sn + ':' + item.name
								};
							}
						}
					});
				}
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
			queryEnumGoal: function (queryString, cb) {
				var items = this.enumGoalSearch;
				var results = queryString ? items.filter(this.createStateFilter(queryString)) : items;
				clearTimeout(this.timeout);
				this.timeout = setTimeout(() => {
					cb(results);
				});
			},
			createStateFilter(queryString) {
				return (state) => {
					return (state.value.indexOf(queryString) !== -1);
				};
			},
			// 更新任务目标
			updateGoalSn: function(sn) {
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_table_data_by_sn(util.getCurrentUserToken(), this.QUESTGOAL, sn),
					success: res => {
						let json = res.data['data']
						if (typeof(json) != typeof('')) {
							this.goalSn = ''
							this.goalVerNum = '-1'
							this.combinGoal = []
							this.baseGoal = {combinationType: 0,
							relationType: 0,
							desc: '没有任务目标！'}
						} else {
							let goalInfo = JSON.parse(json)
							this.questGoalTable.set(goalInfo.sn, goalInfo)
							this.goalSn = sn
							// this.goalVerNum = goalInfo.verNum
							this.goalVerNum = 'ignore'
							this.baseGoal = goalInfo
							this.baseGoal.combinationType = parseInt(goalInfo.combinationType)-1
							this.baseGoal.relationType = parseInt(goalInfo.relationType)
							this.parseCombinGoal()
						}
					}
				});
			},
			// 解析任务目标
			parseCombinGoal: function() {
				this.combinGoal = []
				/** 组合目标 */
				if (this.baseGoal.combinationType == 1) {
					for (var i = 0; i < this.baseGoal.condList.length; i++) {
						this.parseBaseGoal(this.baseGoal.condList[i], i)
					}
				} else {
					this.parseBaseGoal(this.goalSn, 0)
				}
			},
			// 解析基础目标
			parseBaseGoal: function(sn, index) {
				let data = this.questGoalTable.get(sn)
				data.combinationType = parseInt(data.combinationType)
				data.relationType = parseInt(data.relationType)
				if (typeof(data.condList) === typeof('')) {
					data.condList = data.condList.split(",")
				}
				if (typeof(data.params) === typeof('')) {
					data.params = data.params.split(';')
				}
				if (typeof(data.targetPos) === typeof('')) {
					data.targetPos = data.targetPos.split(';')
				}
				this.combinGoal[index] = data
			},
			// 设置任务目标
			setQuestGoal: function (item) {
				let sn = item.value.substring(0, item.value.lastIndexOf(':'))
				console.log('设置任务目标：' + sn)
				this.updateGoalSn(sn)
				util.updateDataFieldNV(this.QUEST, this.tableRowData['sn'], 'goal', sn, this)
			},
			// 修改目标描述
			onGoalDescChange: function(e) {
				if (this.goalSn === '') return;
				util.updateDataField(this.QUESTGOAL, this.goalSn, 'desc', this.baseGoal.desc, this.goalVerNum, this)
			},
			// 修改目标组合类型
			onSelectCombinationType: function(selectIndex) {
				if (this.goalSn === '') return;
				util.updateDataField(this.QUESTGOAL, this.goalSn, 'combinationType', selectIndex+1, this.goalVerNum, this)
			},
			// 修改目标关系类型
			onSelectGoalRelate: function(selectIndex, goalIndex) {
				let sn = this.combinGoal[goalIndex].sn
				this.combinGoal[goalIndex].relationType = selectIndex
				util.updateDataField(this.QUESTGOAL, sn, 'relationType', selectIndex, this.goalVerNum, this)
			},
			// 修改目标类型
			onSelectEnumGoal: function(e, goalIndex, condIndex) {
				let condList = this.combinGoal[goalIndex].condList
				if (condIndex < condList.length) {
					let sn = this.combinGoal[goalIndex].sn
					let type = e.value.split(':')[0]
					condList[condIndex] = type
					let value = condList.join(',')
					util.updateDataField(this.QUESTGOAL, sn, 'condList', value, this.goalVerNum, this)
				}
			},
			// 修改目标参数
			onParamsChange: function(value, goalIndex, condIndex) {
				let params = this.combinGoal[goalIndex].params
				// 填充
				for (let i = params.length; i <= condIndex; i++) {
					params.push('')
					console.log('填充'+i)
				}
				if (condIndex < params.length) {
					let sn = this.combinGoal[goalIndex].sn
					params[condIndex] = value
					let str = params.join(';')
					util.updateDataField(this.QUESTGOAL, sn, 'params', str, this.goalVerNum, this)
				}
			},
			// 修改目标位置
			onPosChange: function(value, goalIndex, condIndex) {
				let pos = this.combinGoal[goalIndex].targetPos
				// 填充
				for (let i = pos.length; i <= condIndex; i++) {
					pos.push('')
					console.log('填充'+i)
				}
				if (condIndex < pos.length) {
					let sn = this.combinGoal[goalIndex].sn
					pos[condIndex] = value
					let str = pos.join(';')
					util.updateDataField(this.QUESTGOAL, sn, 'targetPos', str, this.goalVerNum, this)
				}
			},
			// 移除目标列
			onRemoveGoal: function(goalIndex, condIndex) {
				let goalData = this.combinGoal[goalIndex]
				let sn = goalData.sn
				if (condIndex < goalData.condList.length) {
					goalData.condList.splice(condIndex, 1)
					let str = goalData.condList.join(',')
					util.updateDataField(this.QUESTGOAL, sn, 'condList', str, this.goalVerNum, this)
				}
				if (condIndex < goalData.params.length) {
					goalData.params.splice(condIndex, 1)
					let str = goalData.params.join(';')
					util.updateDataField(this.QUESTGOAL, sn, 'params', str, this.goalVerNum, this)
				}
				if (condIndex < goalData.targetPos.length) {
					goalData.targetPos.splice(condIndex, 1)
					let str = goalData.targetPos.join(';')
					util.updateDataField(this.QUESTGOAL, sn, 'targetPos', str, this.goalVerNum, this)
				}
			},
			// 新增目标列
			onAddGoal: function(goalIndex) {
				let goalData = this.combinGoal[goalIndex]
				goalData.condList.push('0')
				let sn = goalData.sn
				let value = goalData.condList.join(',')
				util.updateDataField(this.QUESTGOAL, sn, 'condList', value, this.goalVerNum, this)
			},
			// 新增任务目标
			onAddQuestGoal: function(e) {
				this.showAddGoal = false
				if (this.newGoalId.length == 0 || this.newGoalId != Math.floor(this.newGoalId) 
					|| Math.floor(this.newGoalId) <= 0) {
					this.$notify.error({
						title: '提交错误',
						message: '任务目标ID必须为正数!!!'
					});
					return;
				}
				// 检查sn
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_table_data_by_sn(util.getCurrentUserToken(), this.QUESTGOAL, this.newGoalId),
					success: res => {
						let json = res.data['data']
						if (typeof(json) == typeof('')) {
							this.$notify.error({
								title: '提交错误',
								message: '任务目标ID已存在请修改!!!'
							});
						}
						else {
							this.showAddGoal = false
							this.$notify.success({
								title: '成功',
								message: '新增任务目标: ' + this.newGoalId
							});
							let goalInfo = {
								sn: this.newGoalId,
								desc: this.newGoalDesc,
								combinationType: 1,
								relationType: 0,
								condList: '0',
								params: '',
								targetPos: '',
								lookAtPos: ''
							}
							util.addDataField(this.QUESTGOAL, JSON.stringify(goalInfo), null, this)
							goalInfo.condList = ['0']
							goalInfo.params['']
							goalInfo.targetPos['']
							this.questGoalTable.set(goalInfo.sn, goalInfo)
							this.questGoalSearch.push({
								value: goalInfo.sn + ':' + goalInfo.desc
							})
						}
					}
				});
			},
			onAddTableData: function(e) {
				
			},
			onEditorBtn: function(e) {
				console.log(e)
			},
			onClickAddQuestGoal: function(e) {
				this.showAddGoal = true
				this.newGoalId = this.goalSn
			},
			onSelect: function(e) {
				
			},
			parseIntArr: function(str) {
				var dataIntArr = []
				var dataStrArr = str.split(",")
				dataStrArr.forEach(function(data, index, arr){
					dataIntArr.push(+data)
				})
				return dataIntArr
			}
		}
	}
</script>

<style>
  view {
  	font-size: 15upx;
  	margin: 2upx;
  
  	display: flex;
  	flex-direction: row;
  }
  
  /* .box-card {
  	  width: 300upx;
  	  margin-bottom: 10upx;
  } */
  
  .el-form {
  	align: middle;
  }
  
  .el-form-item {
  	height: 20upx;
  }
  
  .goal-desc {
	font-size: 18px;
  }
  
  .item-text {
	font-size: 18px;
	width: 10upx;
	padding-left: 20upx;
  }
  
  .clearfix {
  	  font-size: 10upx;
  	  font-weight: bold
  }
  
  .float {
	  position:fixed;
	  top: 20%;
	  right: 9%;
	  text-align: center;
	  box-shadow: 2px 2px 3px #999;
	  z-index: 10;
  }
  
  @keyframes bar {
  	  0% { background-position: 0%; }
  	  100% { background-position: 200%; }
  }
  
  .hr {
	  width: 100%;
	  height: 1upx;
	  display: block;
	  position: relative;
	  margin-bottom: 0upx;
	  padding: 5upx 0;
  }

  .hr::before {
	  content: "";
	  position: absolute;
	
	  width: 100%;
	  height: 1upx;
	  bottom: 50%;
	  left: 0;
	
	  /* background: linear-gradient( 90deg, #10111F 0%, #10111F 50%, transparent 50%, transparent 100% ); */
	  background: linear-gradient( 90deg, #FFFFFF 0%, #FFFFFF 50%, transparent 50%, transparent 100% );
	  background-size: 15px;
	  background-position: center;
	  z-index: 1;
  }

  .hr::after {
	  content: "";
	  position: absolute;
	
	  width: 100%;
	  height: 1px;
	  bottom: 50%;
	  left: 0;
	
	  transition: opacity 0.3s ease, animation 0.3s ease;
	
	  background: linear-gradient(
		  to right, 
		  #62efab 5%, 
		  #F2EA7D 15%, 
		  #F2EA7D 25%, 
		  #FF8797 35%, 
		  #FF8797 45%, 
		  #e1a4f4 55%, 
		  #e1a4f4 65%, 
		  #82fff4 75%, 
		  #82fff4 85%, 
		  #62efab 95%);
	
	  background-size: 200%;
	  background-position: 0%;
	  -webkit-animation: bar 15s linear infinite;
}

  .anim::before {
	  background: linear-gradient( 
	        90deg, 
	        #FFFFFF 0%, #FFFFFF 5%, 
	        transparent 5%, transparent 10%, 
	        #FFFFFF 10%, #FFFFFF 15%, 
	        transparent 15%, transparent 20%, 
	        #FFFFFF 20%, #FFFFFF 25%,
	        transparent 25%, transparent 30%,
	        #FFFFFF 30%, #FFFFFF 35%, 
	        transparent 35%, transparent 40%, 
	        #FFFFFF 40%, #FFFFFF 45%, 
	        transparent 45%, transparent 50%, 
	        #FFFFFF 50%, #FFFFFF 55%,
	        transparent 55%, transparent 60%,
	        #FFFFFF 60%, #FFFFFF 65%,
	        transparent 65%, transparent 70%, 
	        #FFFFFF 70%, #FFFFFF 75%, 
	        transparent 75%, transparent 80%, 
	        #FFFFFF 80%, #FFFFFF 85%,
	        transparent 85%, transparent 90%,
	        #FFFFFF 90%, #FFFFFF 95%, 
	        transparent 95%, transparent 100% );
	
	  background-size: 150px;
	  background-position: center;
	  z-index: 1;
	
	  animation: bar 120s linear infinite;
  }

  .anim:hover::before {
	  animation-duration: 20s;
  }

  .anim:hover::after {
	  animation-duration: 2s;
  }
</style>
