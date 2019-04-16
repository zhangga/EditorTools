<template>
	<view style="flex-direction: column;">
		<!-- 任务目标 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务目标</span>
			</view>
			<el-form label-width="60upx">
				<el-form-item label="目标ID:">
					<textInput :datas="questGoalSearch" placeholder='任务目标' :method='loadTableGoal' :select="setQuestGoal" v-bind:value="goalSn">
					</textInput>
				</el-form-item>
			</el-form>
			
			<el-card class="box-card">
				<view slot="header">
					<el-row>
						<el-col :span="10">
							<span class="header">策划备注</span>
						</el-col>
						<el-col :span="14">
							<el-input type="textarea" v-model="baseGoal.desc" placeholder="备注信息" @change="onGoalDescChange($event)"></el-input>
						</el-col>
					</el-row>
				</view>
				<el-form label-width="60upx">
					<el-form-item label="组合类型">
						<el-select v-model="baseGoal.combinationType" size="medium" id="combinationType" @change="onSelectCombinationType">
							<el-option v-for="index in ConfigCombinType.length" :key="index" :value="index-1" 
								:label="ConfigCombinType[index-1]"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="关系类型" label-width="200upx" v-if="baseGoal.combinationType==1">
						<el-select v-model="baseGoal.relationType" size="medium" id="relationType" @change="onEditorBtn">
							<el-option v-for="index in ConfigRelationType.length" :key="index" :value="index-1" 
								:label="ConfigRelationType[index-1]"></el-option>
						</el-select>
					</el-form-item>
					<el-card class="box-card" v-for="goalIndex in combinGoal.length">
						<el-form label-width="60upx">
							<el-form-item label="关系类型" label-width="220upx">
								<el-select v-model="combinGoal[goalIndex-1].relationType" size="medium" @change="onSelectGoalRelate($event, goalIndex-1)">
									<el-option v-for="index in ConfigRelationType.length" :key="index" :value="index-1" 
										:label="ConfigRelationType[index-1]"></el-option>
								</el-select>
							</el-form-item>
							<el-form-item label="目标类型" v-for="condIndex in combinGoal[goalIndex-1].condList.length" label-width="40upx">
								<el-row>
									<el-col :span="8">
										<el-autocomplete placeholder="请输入内容"
											v-model="combinGoal[goalIndex-1].condList[condIndex-1]" 
											:fetch-suggestions="queryEnumGoal"  @select="onSelectEnumGoal">
										</el-autocomplete>
									</el-col>
									<el-col :span="2">
										<span class="item-text">参数</span>
									</el-col>
									<el-col :span="8">
										<el-input v-model="combinGoal[goalIndex-1].params" placeholder="请输入参数"></el-input>
									</el-col>
									<el-col :span="6">
										<el-input type="textarea" :disabled="true"
											v-model="enumGoalTable.get(combinGoal[goalIndex-1].condList[condIndex-1]).param"></el-input>
									</el-col>
								</el-row>
							</el-form-item>
						</el-form>
					</el-card>
				</el-form>
			</el-card>
			
			<el-popover placement="right-start" width="500" trigger="click" v-model="showItem">
				<el-button slot="reference" type='success' style="float: left">查询物品</el-button>
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">查询物品</span>
					</view>
					<el-form label-width="60upx">
						<el-form-item label="物品信息">
							<textInput :datas="itemSearch" placeholder='物品查询' :method='loadItem' :select="onSelect" v-bind:value="searchText">
							</textInput>
						</el-form-item>
					</el-form>
				</el-card>
			</el-popover>
			
			<el-popover placement="right-start" width="500" trigger="click" v-model="showNpc">
				<el-button slot="reference" type='warning' style="position: absolute; left: 120upx">查询NPC</el-button>
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">查询NP</span>
					</view>
					<el-form label-width="60upx">
						<el-form-item label="NPC信息">
							<textInput :datas="npcSearch" placeholder='NPC查询' :method='loadNpc' :select="onSelect" v-bind:value="searchText">
							</textInput>
						</el-form-item>
					</el-form>
				</el-card>
			</el-popover>
			
			<el-popover placement="right-start" width="500" trigger="click" v-model="showMonster">
				<el-button slot="reference" type='danger' style="position: absolute; left: 240upx">查询怪物</el-button>
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">查询怪物</span>
					</view>
					<el-form label-width="60upx">
						<el-form-item label="怪物信息">
							<textInput :datas="monsterSearch" placeholder='怪物查询' :method='loadMonster' :select="onSelect" v-bind:value="searchText">
							</textInput>
						</el-form-item>
					</el-form>
				</el-card>
			</el-popover>
		</el-card>
		
		<el-popover placement="right-start" width="500" trigger="click" v-model="showAddGoal">
			<el-button slot="reference" type='info' round class="float" style="float: right" @click="onClickAddGoal">新增任务目标</el-button>
			<el-card class="box-card">
				<view slot="header" class="clearfix">
					<span class="header">新增任务目标</span>
				</view>
				<el-form label-width="60upx">
					<el-form-item label="任务目标ID">
						<el-input v-model="newGoalId"></el-input>
					</el-form-item>
					<el-form-item label="策划备注">
						<el-input v-model="newGoalDesc"></el-input>
					</el-form-item>
					<el-form-item>
						<el-button type='primary' style="float: right" @click="onAddGoal">提交</el-button>
					</el-form-item>
				</el-form>
			</el-card>
		</el-popover>
		
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
				/** 任务目标表数据 */
				questGoalTable: new Map(),
				questGoalSearch: [],
				enumGoalTable: new Map(),
				enumGoalSearch: [],
				newGoalId: 0,
				newGoalDesc: '新任务目标',
				goalVerNum: 'ignore',
				// 目标组
				goalSn: '',
				baseGoal: {combinationType: 0,
					relationType: 0,
					desc: '没有任务目标！'},
				combinGoal: [],
				/** 编辑任务目标 */
				showItem: false,
				showNpc: false,
				showMonster: false,
				showAddGoal: false,
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
				data: msg.get_quest_goal_info(this.$store.state.token),
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
						this.enumGoalSearch[i] = {value: item.sn + ':' + item.desc}
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
			loadNpc: function () {
				if (this.npcSearch.length === 0) {
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(this.$store.state.token, "NPC"),
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
						data: msg.get_table_data(this.$store.state.token, "ITEM"),
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
						data: msg.get_table_data(this.$store.state.token, "CHARACTER"),
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
					data: msg.get_table_data_by_sn(this.$store.state.token, this.QUESTGOAL, sn),
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
							this.goalVerNum = goalInfo.verNum
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
				console.log(this.baseGoal.combinationType)
			},
			onGoalNullChange: function(e) {
				console.log(e)
			},
			onEditorBtn: function(e) {
				console.log(e)
			},
			onSelectEnumGoal: function(e) {
				
			},
			onClickAddGoal: function(e) {
				this.newGoalId = this.goal
			},
			onAddGoal: function(e) {
				
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
  
  .item-text {
	font-size: 18px;
	width: 10upx;
	padding-left: 20upx;
  }
</style>
