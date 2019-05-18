<template>
	<view style="flex-direction: column">
		
		<!-- 任务目标 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<el-row>
					<el-col :span='16'>
						<span class="header">任务目标</span>
					</el-col>
					
					<!-- 【新增目标列】悬浮按钮 -->
					<el-col :span='4'>
						<el-button round type="success" icon="el-icon-circle-plus" @click="onAddGoalCondButtonClicked">新增目标列</el-button>
					</el-col>
					
					<!-- 【新增任务目标】悬浮按钮 -->
					<el-col :span='4'>
						<el-button round type='info' icon="el-icon-circle-plus" @click="onClickAddQuestGoalButton">新增任务目标</el-button>
						<el-dialog title="新增任务目标" width="42%" :visible.sync="showAddQuestGoalDialog" :center="true">
							<el-form label-width="60upx" style="width: 85%" :model="addQuestGoalForm" :rules="addQuestGoalRules" ref="addQuestGoalForm">
								<el-form-item label="任务目标ID" prop="newQuestGoalId">
									<el-input-number v-model="addQuestGoalForm.newQuestGoalId" :min="1"></el-input-number>
								</el-form-item>
								<el-form-item label="策划备注" prop="newQuestGoalDesc">
									<el-input v-model="addQuestGoalForm.newQuestGoalDesc" placeholder="请输入任务目标描述信息"></el-input>
								</el-form-item>
							</el-form>
							<span slot="footer" class="dialog-footer">
								<el-button type="primary" @click="onAddQuestGoal">提交</el-button>
								<el-button @click="onCancelAddQuestGoal">取消</el-button>
							</span>
						</el-dialog>
					</el-col>
				</el-row>
			</view>
			
			<!-- 顶层属性 -->
			<el-form label-width="40upx">
				<el-row>
					<!-- 【目标ID】表单项 -->
					<el-col :span="8">
						<el-form-item label="目标ID:">
							<textInput :datas="QuestGoalNames" placeholder='任务目标' :method='loadQuestGoalNames' :select="onSelectQuestGoal" v-bind:value="questGoalObject.sn">
							</textInput>
						</el-form-item>
					</el-col>
					
					<!-- 【备注】表单项 -->
					<el-col :span="8" :offset="3">
						<el-form-item label="备注">
							<el-input type="textarea" :rows="1" v-model="questGoalObject.desc" placeholder="备注信息" @change="onUpdateQuestGoalDesc" 
								:disabled="!hasValidGoal"></el-input>
						</el-form-item>
					</el-col>
				</el-row>
				
				<el-row>
					<!-- 【组合类型】表单项 -->
					<el-col :span="8">
						<el-form-item label="组合类型">
							<el-select v-model="questGoalObject.combinationType" @change="onSelectCombinationType" :disabled="!hasValidGoal">
								<el-option v-for="type in combinationTypes" :key="type.key" :value="type.value" :label="type.value"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					
					<!-- 【关系类型】表单项 -->
					<el-col :span="8" :offset="3">
						<el-form-item label="关系类型">
							<el-select v-model="questGoalObject.relationType" @change="onSelectRelationType" :disabled="!hasValidGoal">
								<el-option v-for="(type, index) in relationTypes" :key="index" :value="type" :label="type"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
				</el-row>
				
				<el-row>
					<!-- 【目标类型】表单项 -->
					<el-col :span="8">
						<el-form-item label="目标类型" v-if="questGoalObject.combinationType == combinationTypes[1].value">
							<el-popover placement="right" width="400" trigger="focus" v-model="isCondListPopoverVisible" title="目标类型列表">
								<el-form v-for="(cond, index) in questGoalObject.condList" v-bind:key="index" style="margin-top: 12upx;padding-left: 10upx">
									<el-row>
										<el-col :span='20'>
											<el-form-item :label="'目标类型' + parseInt(index + 1)">
												<el-autocomplete placeholder="子任务目标"
													v-model="questGoalObject.condList[index]" 
													:fetch-suggestions="queryQuestGoal"  @select="onSelectQuestGoalForCondList($event, index)">
												</el-autocomplete>
											</el-form-item>
										</el-col>
										<el-col :span='3' :offset='1'>
											<el-form-item label="" size="mini">
												<el-button circle type='danger' icon="el-icon-minus" @click="onClickedRemoveCondListItemButton(index)"></el-button>
											</el-form-item>
										</el-col>
									</el-row>
								</el-form>
								
								<el-row>
									<el-col :offset="15">
										<el-button round type='info' icon="el-icon-circle-plus" @click="onClickedAddCondListItemButton" size="small">新增目标类型</el-button>
									</el-col>
								</el-row>
								
								<el-input placeholder="任务目标列表" slot="reference" v-model="strCondList">
								</el-input>
							</el-popover>		
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			
			<el-card v-for="(goal, goalIndex) in subGoals" v-bind:key="goalIndex" shadow="never" body-style="padding-top:10upx; padding-bottom: 0upx;" style="margin-bottom: 5upx">
					<!-- 当组合类型为【复合类型】时显示卡片对应的子任务目标描述 -->
					<view slot="header" style="font-size: 8upx" v-if="questGoalObject.combinationType == combinationTypes[1].value">
						<span class="header">子任务{{goalIndex + 1}} {{goal.desc}}</span>
					</view>
					
					<!-- 子任务的顶层属性 -->
					<el-form label-width="40upx" v-if="questGoalObject.combinationType == combinationTypes[1].value">
						<el-row>
							<!-- 【组合类型】表单项 -->
							<el-col :span="7">
								<el-form-item label="组合类型">
									<!-- 嵌套的子任务默认将无法选择组合类型，以避免嵌套的复合目标类型 -->
									<el-select v-model="goal.combinationType" :disabled="true">
										<el-option v-for="type in combinationTypes" :key="type.key" :value="type.value" :label="type.value"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
							
							<!-- 【关系类型】表单项 -->
							<el-col :span="6" :offset="1">
								<el-form-item label="关系类型">
									<el-select v-model="goal.relationType" @change="onSelectSubQuestRelationType($event, goalIndex)" :disabled="!hasValidGoal">
										<el-option v-for="(type, index) in relationTypes" :key="index" :value="type" :label="type"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
							
							<!-- 【备注】表单项 -->
							<el-col :span="6" :offset="1">
								<el-form-item label="备注">
									<el-input type="textarea" :rows="1" v-model="goal.desc" placeholder="备注信息" @change="onUpdateSubQuestGoalDesc" 
										:disabled="!hasValidGoal"></el-input>
								</el-form-item>
							</el-col>
						</el-row>
						
						<!-- 分割线 -->
						<rainbow-divider class="divider" style="margin-top: 2upx"></rainbow-divider>
					</el-form>
					
					<el-form v-for="(cond, condIndex) in goal.condList" v-bind:key="condIndex" label-width="40upx">
						<el-row>
							<!-- 【目标类型】表单项 -->
							<el-col :span="6">
								<el-form-item label="目标类型">
									<el-autocomplete placeholder="请输入目标类型" v-model="goal.condList[condIndex]"
										:fetch-suggestions="queryQuestGoalType"  @select="onSelectQuestGoalType($event, goalIndex, condIndex)">
									</el-autocomplete>
								</el-form-item>
							</el-col>
							
							<!-- 【参数】表单项 -->
							<el-col :span="7" :offset="1">
								<el-form-item label="参数">
									<el-input v-model="goal.params[condIndex]" @change="onUpdateParams($event, goalIndex, condIndex)" 
										placeholder="请输入参数"></el-input>
								</el-form-item>
							</el-col>
							
							<el-col :span="7" :offset="1">
								<el-form-item label="朝向位置">
									<el-input v-model="goal.lookAtPos[condIndex]" @change="onUpdateLookAtPos($event, goalIndex, condIndex)" 
										placeholder="请输入朝向位置"></el-input>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<!-- 【目标位置】表单项 -->
							<el-col :span="8">
								<el-form-item label="目标位置">
									<!-- TODO: 更换el-input为通过dialog添加 -->
									<el-input v-model="goal.targetPos[condIndex]" @change="onUpdateTargetPos($event, goalIndex, condIndex)" 
										placeholder="请输入目标位置"></el-input>
								</el-form-item>
							</el-col>
							
							<!-- 【参数说明】表单项 -->
							<el-col :span="9" :offset="2">
								<el-form-item label="参数说明">
									<!-- TODO：解决WARNING -->
									<el-input :disabled="true" 
										v-model="QuestGoalTypes.get(goal.condList[condIndex]) == null ? '暂无' : 
											QuestGoalTypes.get(goal.condList[condIndex]).param"></el-input>
								</el-form-item>
							</el-col>
							
							<!-- 【删除】按钮 -->
							<el-col :span="2" :offset="1">
								<el-form-item label-width="0upx" style="">
									<el-button type="danger" icon="el-icon-remove" @click="onRemoveGoal(goalIndex, condIndex)">删除</el-button>
								</el-form-item>
							</el-col>
						</el-row>
						
						<!-- 分割线 -->
						<rainbow-divider v-if="condIndex != goal.condList.length - 1" class="divider" style="margin-top: 2upx"></rainbow-divider>
					</el-form>
			</el-card>

			<el-row style="margin-top: 5upx">
				<el-col :span="4">
					<el-button type='success' icon="el-icon-search" @click="showItemDialog = true">查询物品</el-button>
					<el-dialog title="查询物品" width="42%" :visible.sync="showItemDialog" center>
						<el-form label-width="120upx">
							<el-form-item label="物品信息">
								<textInput :datas="itemSearch" placeholder='物品查询' :method='loadItemData' :select="onSelect" v-bind:value="searchText">
								</textInput>
							</el-form-item>
						</el-form>
						<span slot="footer" class="dialog-footer">
							<el-button type="primary" @click="showItemDialog = false">完成</el-button>
						</span>
					</el-dialog>
				</el-col>
				<el-col :span="4" :offset="6">
					<el-button type='warning' icon="el-icon-search" @click="showNpcDialog = true">查询NPC</el-button>
					<el-dialog title="NPC查询" width="42%" :visible.sync="showNpcDialog" center>
						<el-form label-width="120upx">
							<el-form-item label="NPC信息">
								<textInput :datas="npcSearch" placeholder='NPC查询' :method='loadNpcData' :select="onSelect" v-bind:value="searchText">
								</textInput>
							</el-form-item>
						</el-form>
						<span slot="footer" class="dialog-footer">
							<el-button type="primary" @click="showNpcDialog = false">完成</el-button>
						</span>
					</el-dialog>
				</el-col>
				<el-col :span="4" :offset="6">
					<el-button type='danger' icon="el-icon-search" @click="showMonsterDialog = true">查询怪物</el-button>
						<el-dialog title="怪物查询" width="42%" :visible.sync="showMonsterDialog" center>
							<el-form label-width="120upx">
								<el-form-item label="怪物信息">
									<textInput :datas="monsterSearch" placeholder='怪物查询' :method='loadMonsterData' :select="onSelect" v-bind:value="searchText">
									</textInput>
								</el-form-item>
							</el-form>
							<span slot="footer" class="dialog-footer">
								<el-button type="primary" @click="showMonsterDialog = false">完成</el-button>
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
	import rainbowDivider from '../component/rainbowDivider.vue'
	import config from '../../common/config.js'
	import msg from '../../common/msg.js'
	import util from '../../common/util.js'
	
	export default {
		data() {
			return {
				/* 数据结构 */
				questGoalObject: {									// 任务目标数据对象
					sn: '',
					desc: '',
					condList: [],
					params: [],
					targetPos: [],
					lookAtPos: [],
					relationType: '',								// 关系类型（0：单个，1：与关系，2：或关系）
					combinationType: '',							// 组合类型（1：基础目标，2：复合目标）
				},
				combinationTypes: [{								// 组合类型数据结构
					key: '1',
					value: "基础目标"
				}, {
					key: '2',
					value: "复合目标"
				}],		
				
				// 本地数据缓存
				relationTypes: [],									// 记录所有的【关系类型】选项
				subGoals: [],										// 针对【复合目标】类型，记录所有子目标相关数据 | 针对【基础目标类型】，记录当前目标相关数据
				QuestGoalTypes: new Map(),							// Map类型，记录所有任务目标类型（QuestGoalType）的SN与QuestGoalType数据的键值对
				QuestGoalTypeNames: [],								// 记录所有任务目标类型（QuestGoalType）的名称（SN:DESC格式）
				QuestGoals: [], 									// 记录所有任务目标（QuestGoal）的数据（按照 {key: SN, value: DATA} 格式）
				QuestGoalNames: [],									// 记录所有任务目标（QuestGoal）的名称（SN:DESC格式）
				itemSearch: [],										// 物品表数据缓存
				npcSearch: [],										// NPC表数据缓存
				monsterSearch: [],									// 怪物表数据缓存
				
				/* 新增任务目标相关变量 */
				showAddQuestGoalDialog: false,
				addQuestGoalForm: {									// 新增任务目标表单数据封装
					newQuestGoalId: 0,								// 新增任务目标ID
					newQuestGoalDesc: '',							// 新增任务目标描述
				},
				addQuestGoalRules: {									// 新增任务目标表单规则
					newQuestGoalId: [
						{required: true, message: "请提供合法的任务目标ID", trigger: 'blur'}
					],
					newQuestGoalDesc: [
						{required: true, message: "请提供新增任务目标的描述信息", trigger: 'blur'}
					]
				},
				
				/* 编辑目标列表弹窗相关变量 */
				isCondListPopoverVisible: false,
				strCondList: '',									// String格式的condList

				/* 全局变量 */
				QUEST: 'QUEST',										// QUEST表格名
				QUESTGOAL: 'QUESTGOAL',								// QUESTGOAL表格名
				prevTableRowData: null,
				hasSetDefaultValue: false,
				hasSetQuestGoalRelatedDefaultValues: false,
				hasSetQuestGoalTypeRelatedDefaultValues: false,
				shouldReloadQuestGoal: false,						// 是否需要强制刷新缓存的QuestGoal数据
				shouldReloadQuestGoalType: false,					// 是否需要强制刷新缓存的QuestGoalType数据
				activeQuestGoal: '',
				showMonsterDialog: false,							// 是否显示怪物数据弹窗
				showNpcDialog: false,								// 是否显示NPC数据弹窗
				showItemDialog: false,								// 是否显示物品数据弹窗
				searchText: '',										// 绑定弹窗中输入的搜索关键字
				loadingInstance: null,								// 加载中（loading）界面的实例
				hasValidGoal: false,								// 标识当前记录是否有合法的完成目标
				relationTypeNotifyInstance: null					// 选择关系类型时警告弹窗的实例
			};
		},
		mounted: function() {
			// 获取QuestGoal相关数据
			this.loadQuestGoalInfo()
			this.loadQuestGoalNames()
			this.loadQuestGoalTypeNames()
			
			// 预先加载表数据
			this.loadItemData()
			this.loadNpcData()
			this.loadMonsterData()
			
			this.relationTypes = config.ExcelConfig().RelationType
		},
		updated: function() {	
			// 更新目标ID合法标识
			this.hasValidGoal = (this.questGoalObject.sn != null && this.questGoalObject.sn.length != 0)
			
			// 如果是用户输入触发的更新，不刷新默认值
			if (this.hasSetDefaultValue) {
				// 如果是由于切换任务导致的更新，刷新默认值
				if (this.prevTableRowData != this.tableRowData) {
					this.refreshDefaultValues()
					this.prevTableRowData = this.tableRowData
					
					// 数据刷新后需要重新检测是否有合法的Condition ID
					this.hasValidGoal = (this.questGoalObject.sn != null && this.questGoalObject.sn.length != 0)
					
					// 由于任务接取页面涉及到对Condition表的操作
					// 切换页面时需要获取新记录【条件ID】对应Condition记录的版本号（VerNum）
					if (this.hasValidGoal) {
						uni.request({
							url: msg.url(),
							method: 'GET',
							data: msg.get_version_number_by_sn(util.getCurrentUserToken(), 'QUESTGOAL', this.questGoalObject.sn.split(':')[0]),
							success: res => {
								this.$store.state.verNum.set('QUESTGOAL', res.data.verNum)
							},
							fail: () => {
								this.$store.state.verNum.set('QUESTGOAL', 'ignore')
							},
							complete: () => {
								console.log('获取QUESTGOAL中记录为' + this.questGoalObject.sn.split(':')[0] + '的版本号为' + this.$store.state.verNum.get('QUESTGOAL'))
							}
						})
					}
				}
			}
		},
		watch: {
			strCondList: function () {
				this.questGoalObject.condList = this.strCondList.split(',')
			}
		},
		methods: {
			// 数据初始化
			refreshDefaultValues: function() {
				this.questGoalObject.sn = this.tableRowData['goal'] != null ? this.findQuestGoalName(this.tableRowData['goal']) : null
				let questGoalDetails = this.findQuestGoal(this.tableRowData['goal'])
				
				// 清空缓存
				this.subGoals = []
				
				// 考虑数据尚未初始化完毕的情况
				if (questGoalDetails == this.tableRowData['goal']) {
					console.log("FAILED TO REFRESH")
					return
				} else {
					this.questGoalObject.condList = questGoalDetails.condList.split(',')
				}
				
				if (questGoalDetails.combinationType == '2') {					// 如果【组合类型】为【复合目标】
					// 初始化子任务目标
					for (let i = 0; i < this.questGoalObject.condList.length; i++) {
						console.log("REFRESH VALUES FOR COMPOSITE GOAL TYPE")
						
						let currQuestGoalObject = {}
						let currQuestGoalSn = this.questGoalObject.condList[i]
						let currQuestGoalDetails = this.findQuestGoal(currQuestGoalSn)
						let isQuestGoalValid = (currQuestGoalDetails != currQuestGoalSn)
						
						currQuestGoalObject.sn = isQuestGoalValid ? this.findQuestGoalName(currQuestGoalSn) : ''
						currQuestGoalObject.desc = isQuestGoalValid ? currQuestGoalDetails.desc : ''
						currQuestGoalObject.condList = isQuestGoalValid ? currQuestGoalDetails.condList.split(',') : questGoalDetails.condList.split(',')
						currQuestGoalObject.params = isQuestGoalValid ? currQuestGoalDetails.params.split(';') : []
						currQuestGoalObject.lookAtPos = isQuestGoalValid ? currQuestGoalDetails.lookAtPos : []
						currQuestGoalObject.targetPos = isQuestGoalValid ? currQuestGoalDetails.targetPos.split(';') : []
						currQuestGoalObject.combinationType = isQuestGoalValid ? this.findCombinationType(currQuestGoalDetails.combinationType) : ''
						currQuestGoalObject.relationType = isQuestGoalValid ? this.findRelationType(currQuestGoalDetails.relationType) : ''
						
						this.subGoals.push(currQuestGoalObject)
					}
					
					// 初始化父任务目标内容
					let currQuestGoalSn = this.tableRowData['goal']
					let isQuestGoalValid = (questGoalDetails != currQuestGoalSn)
					
					this.questGoalObject.desc = isQuestGoalValid ? questGoalDetails.desc : ''
					
					this.questGoalObject.combinationType = isQuestGoalValid ? this.findCombinationType(questGoalDetails.combinationType) : ''
					this.questGoalObject.relationType = isQuestGoalValid ? this.findRelationType(questGoalDetails.relationType) : ''
					
					this.strCondList = questGoalDetails.condList
				} else {														// 如果【组合类型】为【基础目标】
					console.log("REFRESH VALUES FOR BASIC GOAL TYPE")

					let currQuestGoalSn = this.tableRowData['goal']
					let currQuestGoalDetails = this.findQuestGoal(currQuestGoalSn)
					let isQuestGoalValid = (currQuestGoalDetails != currQuestGoalSn)
					
					this.questGoalObject.desc = isQuestGoalValid ? currQuestGoalDetails.desc : ''
					this.questGoalObject.condList = isQuestGoalValid ? currQuestGoalDetails.condList.split(',') : []
					this.questGoalObject.params = isQuestGoalValid ? currQuestGoalDetails.params.split(';') : []
					this.questGoalObject.lookAtPos = isQuestGoalValid ? currQuestGoalDetails.lookAtPos : []
					this.questGoalObject.targetPos = isQuestGoalValid ? currQuestGoalDetails.targetPos.split(';') : []
					this.questGoalObject.combinationType = isQuestGoalValid ? this.findCombinationType(currQuestGoalDetails.combinationType) : ''
					this.questGoalObject.relationType = isQuestGoalValid ? this.findRelationType(currQuestGoalDetails.relationType) : ''
					
					this.subGoals.push(this.questGoalObject)
				}
				
				this.hasSetDefaultValue = true
			},
			
			/* -------------------- 通过ID找到对应表项的文字描述（用于数据初始化） -------------------- */
			
			// 根据QuestGoalID找到对应的QuestGoal详细描述信息
			findQuestGoal: function(questGoalID) {
				if (questGoalID == '' || this.QuestGoals.length == 0) {
					return questGoalID
				}
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == questGoalID) {
						return this.QuestGoals[i].value
					}
				}
				return questGoalID
			},
			
			// 根据QuestGoalID找到对应的QuestGoal描述Tag
			findQuestGoalName: function(questGoalID) {
				if (questGoalID == '' || this.QuestGoalNames.length == 0) {
					return questGoalID
				}
				
				for (let i = 0; i < this.QuestGoalNames.length; i++) {
					if (this.QuestGoalNames[i].key == questGoalID) {
						return this.QuestGoalNames[i].value
					}
				}
				return questGoalID
			},
			
			// 根据combinationType的序号找到对应的combinationType描述
			findCombinationType: function(type) {
				if (type == '') {
					return type
				}
				for (let i = 0; i < this.combinationTypes.length; i++) {
					if (this.combinationTypes[i].key == type) {
						return this.combinationTypes[i].value
					}
				}
			},
			
			// 根据relationType的序号找到对应的relationType描述
			findRelationType: function(type) {
				if (type == '') {
					return type
				}
				
				this.relationTypes = config.ExcelConfig().RelationType
				
				// 检查顶层任务是否存在配置问题
				if (this.questGoalObject.condList.length > 1) {
					// 策划配置出错，配置了多个条件，但关系类型配置为了【单个】
					if (type == 0) {
						// 关闭之前显示的弹窗，避免重复弹窗
						if (this.relationTypeNotifyInstance != null) {
							this.relationTypeNotifyInstance.close()
						}
						this.relationTypeNotifyInstance = this.$notify({
							title: '配置出错',
							message: '请检查QuestGoal表格配置数据：条件列表中配置了多个条件，但关系类型（relationType)配置为了【单个】',
							type: 'warning'
						});
						
					}
				}
				
				// 对于复合目标类型，逐个检查子任务是否存在配置问题
				for (let i = 0; i < this.subGoals.length; i++) {
					if (this.subGoals[i].condList.length > 1) {
						// 策划配置出错，配置了多个条件，但关系类型配置为了【单个】
						if (type == 0) {
							// 关闭之前显示的弹窗，避免重复弹窗
							if (this.relationTypeNotifyInstance != null) {
								this.relationTypeNotifyInstance.close()
							}
							this.relationTypeNotifyInstance = this.$notify({
								title: '配置出错',
								message: '请检查QuestGoal表格配置数据：条件列表中配置了多个条件，但关系类型（relationType)配置为了【单个】',
								type: 'warning'
							});
						}
					}
				}
				
				return this.relationTypes[parseInt(type)]
			},
			
			// 根据QuestGoalType ID找到对应的QuestGoalType内容
			findQuestGoalType: function(questGoalID) {
				if (questGoalID == '' || this.QuestGoalTypes.length == 0) {
					return questGoalID
				}
				
				for (let [key, value] of this.QuestGoalTypes) {
					if (key == questGoalID) {
						return value
					}
				}
				return questGoalID
			},
			
			// 根据QuestGoalType ID找到对应的QuestGoalType描述
			findQuestGoalTypeName: function(questGoalID) {
				if (questGoalID == '' || this.QuestGoalTypeNames.length == 0) {
					return questGoalID
				}
				
				for (let name of this.QuestGoalTypeNames) {
					if (name.value.split(':')[0] == questGoalID) {
						return name.value
					}
				}
				return questGoalID
			},
			
			/* -------------------- 获取数据 -------------------- */
			
			// 读取QuestGoal表格以及QuestGoalType表格所有数据
			loadQuestGoalInfo: function() {
				// 重置缓存
				this.QuestGoalTypes = new Map()
				this.QuestGoalTypeNames = []
				
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_quest_goal_info(util.getCurrentUserToken()),
					success: res => {
						let allQuestGoals = res.data['data']
						for (let i = 0; i < allQuestGoals.length; i++) {
							let perQuestGoal = JSON.parse(allQuestGoals[i])
							this.QuestGoals.push({key: perQuestGoal.sn, value: perQuestGoal})
						}
						
						let allQuestGoalTypes = res.data['enumGoal']
						for (let i = 0; i < allQuestGoalTypes.length; i++) {
							this.QuestGoalTypes.set(allQuestGoalTypes[i].sn, allQuestGoalTypes[i])
							this.QuestGoalTypeNames.push({value: allQuestGoalTypes[i].sn + ':' + allQuestGoalTypes[i].desc})
						}
						
						this.refreshDefaultValues()
					},
					fail: () => {},
					complete: () => {}
				});
			},
			// 读取QuestGoal数据表
			loadQuestGoalNames: function() {
				util.loadTableData(this.QuestGoalNames, 'QUESTGOAL', 'sn', 'desc', this.onFinishedLoadingQuestGoalNames, this, this.shouldReloadQuestGoal)
			},
			// 读取QuestGoal数据表的回调方法
			onFinishedLoadingQuestGoalNames: function() {
				this.shouldReloadQuestGoal = false
				
				if (!this.hasSetQuestGoalRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetQuestGoalRelatedDefaultValues = true
				}
			},
			
			// 读取QuestGoalType描述信息
			loadQuestGoalTypeNames: function() {
				if (!this.hasSetQuestGoalTypeRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetQuestGoalTypeRelatedDefaultValues = true
				}
				
				return this.QuestGoalTypeNames
			},
			
			// 根据提供的搜索字符串，查找相匹配的任务目标类型
			queryQuestGoalType: function(queryString, cb) {
				let items = this.QuestGoalTypeNames
				let results = queryString ? items.filter(this.createFilter(queryString)) : items
				cb(results)
			},
			
			// 根据提供的搜索字符串，查找相匹配的任务目标类型
			queryQuestGoal: function(queryString, cb) {
				let items = this.QuestGoalNames
				let results = queryString ? items.filter(this.createFilter(queryString)) : items
				cb(results)
			},
			
			// 字符串匹配规则
			createFilter: function(queryString) {
				return (name) => {
					return (name.value.indexOf(queryString) !== -1)
				}
			},
			// 加载物品表
			loadItemData: function() {
				util.loadTableData(this.itemSearch, "ITEM", "sn", "name")
			},
			// 加载NPC表
			loadNpcData: function() {
				util.loadTableData(this.npcSearch, "NPC", "sn", "name")
			},
			// 加载怪物表
			loadMonsterData: function() {
				util.loadTableData(this.monsterSearch, "CHARACTER", "sn", "name")
			},
			
			/* -------------------- 监听事件 -------------------- */
			
			// 设置任务目标的事件
			onSelectQuestGoal: function(item) {
				let updatedSn = item.value.substring(0, item.value.lastIndexOf(':'))
				
				// 更新本地缓存（QUEST）内容
				this.tableRowData['goal'] = updatedSn
				
				util.updateDataField(this.QUEST, this.tableRowData['sn'], 'goal', updatedSn, this.$store.state.verNum.get(this.QUEST), this, this.refreshDefaultValues())
			},
			
			// 设置目标列表（只用于编辑复合目标的目标列表）
			onSelectQuestGoalForCondList: function(item, condIndex) {
				let questGoalSn = item.value.substring(0, item.value.lastIndexOf(':'))
				
				// 检查设置的子目标的组合类型是否为复合目标，若是，拒绝添加当前子任务
				let isSubQuestValid = true
				
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_table_data_by_sn(util.getCurrentUserToken(), 'QUESTGOAL', questGoalSn),
					success: res => {
						let subQuestGoalDetails = JSON.parse(res.data['data'])
						
						if (subQuestGoalDetails.combinationType == '2') {
							this.$notify.error({
								title: '添加失败',
								message: '不允许添加嵌套的【复合目标】类型任务，请选择其他任务！'
							})
							isSubQuestValid = false
						}
					},
					fail: res => {
						this.$notify.error({
							title: '添加失败',
							message: '服务器出错，请重试！'
						});
						isSubQuestValid = false
					},
					complete: res => {
						if (!isSubQuestValid) {
							return
						}
						
						console.log('设置第' + condIndex + '个任务目标ID为：' + questGoalSn)
						
						this.questGoalObject.condList[condIndex] = questGoalSn
						
						// 更新本地缓存（QUESTGOAL）内容
						let currID = this.questGoalObject.sn.split(':')[0]
						let currCondList = this.questGoalObject.condList.join(',')
						
						for (let i = 0; i < this.QuestGoals.length; i++) {
							if (this.QuestGoals[i].key == currID) {
								this.QuestGoals[i].value.condList = currCondList
								break
							}
						}
						
						util.updateDataField(this.QUESTGOAL, currID, 'condList', currCondList, this.$store.state.verNum.get('QUESTGOAL'), this, this.refreshDefaultValues())
					}
				});
			},
			
			// 修改目标描述
			onUpdateQuestGoalDesc: function() {
				if (this.questGoalObject.sn == '') {
					return;
				}
				
				// 更新本地缓存（QUESTGOAL）内容
				let currID = this.questGoalObject.sn.split(':')[0]
				let currDesc = this.questGoalObject.desc
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.desc = currDesc
						break
					}
				}
				
				// 重新加载QuestGoal相关表单项的初始值
				this.shouldReloadQuestGoal = true
				this.hasSetQuestGoalRelatedDefaultValues = false
				
				util.updateDataField(this.QUESTGOAL, currID, 'desc', currDesc, this.$store.state.verNum.get('QUESTGOAL'), this, this.loadQuestGoalNames)
			},
			
			// 修改子任务的目标描述
			onUpdateSubQuestGoalDesc: function(value, goalIndex) {
				// 更新本地缓存（QUESTGOAL）内容
				let currID = this.subGoals[goalIndex].sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.desc = value
						break
					}
				}
				
				// 重新加载QuestGoal相关表单项的初始值
				this.shouldReloadQuestGoal = true
				this.hasSetQuestGoalRelatedDefaultValues = false
				
				// 子任务的版本号延迟到要更新时再获取
				let subQuestVerNum = 'ignore'
				
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_version_number_by_sn(util.getCurrentUserToken(), 'QUESTGOAL', currID),
					success: res => {
						subQuestVerNum = res.data.verNum
					},
					complete: () => {
						util.updateDataField(this.QUESTGOAL, currID, 'desc', value, subQuestVerNum, this, this.loadQuestGoalNames)
					}
				})
			},
			
			// 更新【组合类型】表单项内容时触发的事件
			onSelectCombinationType: function(value) {
				if (this.questGoalObject.sn == '') {
					return;
				}
				
				let selectedCombinationType = 0
				
				// 找到value对应的组合类型ID
				for (let i = 0; i < this.combinationTypes.length; i++) {
					if (value == this.combinationTypes[i].value) {
						selectedCombinationType = this.combinationTypes[i].key
						break
					}
				}
				
				this.questGoalObject.combinationType = value
				
				// 修改组合类型（从基础目标-复合目标）可能导致目标类型ID索引不到对应的任务目标ID
				// 当组合类型为基础目标时，condList对应的是目标类型ID；而当组合类型为复合目标时，condList对应的是任务目标ID
				// 因此，需要检查condList中的值在切换后是否仍然有效
				if (selectedCombinationType == '2') {														// 若从【基础目标】切换为【复合目标】
					for (let i = 0; i < this.subGoals.length; i++) {
						for (let j = 0; j < this.subGoals[i].condList.length; j++) {
							let currCondId = this.subGoals[i].condList[j]
							// 若无法找到对应的任务目标记录，提示用户数据可能有错
							if (this.findQuestGoalName(currCondId) == currCondId) {
								this.$notify.error({
									title: '无效的【目标类型】',
									message: '修改【组合类型】为【复合目标】后，提供的【目标类型】参数不是合法的任务目标（QuestGoal）ID，请修改数据！',
									duration: 9000
								});
								break
							}
						}
					}
				} else {																					// 若从【复合目标】切换为【基础目标】
					for (let i = 0; i < this.subGoals.length; i++) {
						for (let j = 0; j < this.subGoals[i].condList.length; j++) {
							let currCondId = this.subGoals[i].condList[j]
							// 若无法找到对应的任务目标记录，提示用户数据可能有错
							if (this.findQuestGoalType(currCondId) == currCondId) {
								this.$notify.error({
									title: '无效的【目标类型】',
									message: '修改【组合类型】为【复合目标】后，提供的【目标类型】参数不是合法的任务目标类型（QuestGoalType）ID，请修改数据！',
									duration: 9000
								});
								break
							}
						}
					}
				}
				
				// 更新本地缓存（QUESTGOAL）内容
				let currID = this.questGoalObject.sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.combinationType = selectedCombinationType
						break
					}
				}
				
				util.updateDataField(this.QUESTGOAL, currID, 'combinationType', selectedCombinationType, this.$store.state.verNum.get('QUESTGOAL'), this, this.refreshDefaultValues)
			},
			
			// 更新目标【关系类型】（RelationType）表单项内容时触发的事件
			onSelectRelationType: function(value) {
				if (this.questGoalObject.sn == '') {
					return;
				}
				
				let selectedRelationTypeIndex = 0
				
				// 找到value对应的关系类型ID
				for (let i = 0; i < this.relationTypes.length; i++) {
					if (value == this.relationTypes[i]) {
						selectedRelationTypeIndex = i
						break
					}
				}
				
				// 更新本地缓存（QUESTGOAL）内容
				let currID = this.questGoalObject.sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.relationType = selectedRelationTypeIndex
						break
					}
				}
				
				util.updateDataField(this.QUESTGOAL, currID, 'relationType', selectedRelationTypeIndex, this.$store.state.verNum.get('QUESTGOAL'), this)
			},
			
			// 更新子任务目标的关系类型时的事件
			onSelectSubQuestRelationType: function(value, goalIndex) {
				if (this.questGoalObject.sn == '') {
					return;
				}
				
				let selectedRelationTypeIndex = 0
				
				// 找到value对应的关系类型ID
				for (let i = 0; i < this.relationTypes.length; i++) {
					if (value == this.relationTypes[i]) {
						selectedRelationTypeIndex = i
						break
					}
				}
				
				// 更新本地缓存（QUESTGOAL）内容
				let currID = this.subGoals[goalIndex].sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.relationType = selectedRelationTypeIndex
						break
					}
				}
				
				// 子任务的版本号延迟到要更新时再获取
				let subQuestVerNum = 'ignore'
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_version_number_by_sn(util.getCurrentUserToken(), 'QUESTGOAL', currID),
					success: res => {
						subQuestVerNum = res.data.verNum
					},
					complete: () => {
						util.updateDataField(this.QUESTGOAL, currID, 'relationType', selectedRelationTypeIndex, subQuestVerNum, this)
					}
				})
			},
			
			// 更新【任务目标类型】（QuestGoalType.condList）表单项内容时触发的事件
			onSelectQuestGoalType: function(e, goalIndex, condIndex) {
				let currCondList = this.subGoals[goalIndex].condList
				currCondList[condIndex] = e.value.split(':')[0]
				console.log("更新第" + goalIndex + "个子目标（Goal）的第" + condIndex + "个目标类型为" + currCondList.join(','))
				
				// 更新本地缓存（QUESTGOAL）内容
				let updatedCondList = currCondList.join(',')
				let currID = this.subGoals[goalIndex].sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.condList = updatedCondList
						break
					}
				}
				
				util.updateDataField(this.QUESTGOAL, currID, 'condList', updatedCondList, this.$store.state.verNum.get('QUESTGOAL'), this)
			},
			
			// 更新【任务目标参数】（QuestGoalType.params）表单项内容时触发的事件
			onUpdateParams: function(value, goalIndex, condIndex) {
				let currParams = this.subGoals[goalIndex].params
				currParams[condIndex] = value
				
				// 更新本地缓存（QUESTGOAL）内容
				let updatedParams = currParams.join(';')
				let currID = this.subGoals[goalIndex].sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.params = updatedParams
						break
					}
				}
				
				util.updateDataField(this.QUESTGOAL, currID, 'params', updatedParams, this.$store.state.verNum.get('QUESTGOAL'), this)
			},
			
			// 更新【任务目标朝向位置】（QuestGoalType.lookAtPos）表单项内容时触发的事件
			onUpdateLookAtPos: function(value, goalIndex, condIndex) {
				let currLookAtPos = this.subGoals[goalIndex].lookAtPos
				currLookAtPos[condIndex] = value
				
				// 更新本地缓存（QUESTGOAL）内容
				let updatedLookAtPos = currLookAtPos.join(';')
				let currID = this.subGoals[goalIndex].sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.lookAtPos = updatedLookAtPos
						break
					}
				}
				
				util.updateDataField(this.QUESTGOAL, currID, 'lookAtPos', updatedLookAtPos, this.$store.state.verNum.get('QUESTGOAL'), this)
			},
			
			// 更新【目标位置】（QuestGoalType.params）表单项内容时触发的事件
			onUpdateTargetPos: function(value, goalIndex, condIndex) {
				let currPos = this.subGoals[goalIndex].targetPos
				currPos[condIndex] = value
				
				// 更新本地缓存（QUESTGOAL）内容
				let updatedPos = currPos.join(';')
				let currID = this.subGoals[goalIndex].sn.split(':')[0]
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.params = updatedParams
						break
					}
				}
				
				util.updateDataField(this.QUESTGOAL, currID, 'targetPos', currPos, this.$store.state.verNum.get('QUESTGOAL'), this)
			},
			
			// 点击目标类型弹窗中的【新增目标类型】按钮时的触发事件
			onClickedAddCondListItemButton: function() {
				// 更新缓存内容
				this.questGoalObject.condList.push('')
				
				let currID = this.tableRowData.sn.split(':')[0]
				let currCondList = this.questGoalObject.condList.join(',')
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.condList = currCondList
						break
					}
				}
				
				this.strCondList = currCondList
			},
			
			// 点击目标类型弹窗中的【新增目标类型】按钮时的触发事件
			onClickedRemoveCondListItemButton: function(condIndex) {
				// 更新缓存内容
				this.questGoalObject.condList.splice(condIndex, 1)
				
				let currID = this.tableRowData.sn.split(':')[0]
				let currCondList = this.questGoalObject.condList.join(',')
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.condList = currCondList
						break
					}
				}
				
				this.strCondList = currCondList
				
				util.updateDataField(this.QUESTGOAL, currID, 'condList', currCondList, this.$store.state.verNum.get('QUESTGOAL'), this, this.refreshDefaultValues)
			},
			
			// 点击【移除目标列】按钮的点击事件
			onRemoveGoal: function(goalIndex, condIndex) {
				let goalData = this.subGoals[goalIndex]
				let currID = goalData.sn.split(':')[0]
				
				// 更新本地缓存
				goalData.condList.splice(condIndex, 1)
				goalData.params.splice(condIndex, 1)
				goalData.targetPos.splice(condIndex, 1)
				
				let updatedCondList = goalData.condList.join(',')
				let updatedParams = goalData.params.join(';')
				let updatedTargetPos = goalData.targetPos.join(';')
				
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.condList = updatedCondList
						this.QuestGoals[i].value.params = updatedParams
						this.QuestGoals[i].value.targetPos = updatedTargetPos
					}
				}
				
				// 构建JSON格式的参数
				let valueKeys = {'condList': updatedCondList, 'params': updatedParams, 'targetPos': updatedTargetPos}
				
				// 向服务器请求更新数据
				util.updateMultipleDataInSameRow(this.QUESTGOAL, currID, JSON.stringify(valueKeys), this.$store.state.verNum.get('QUESTGOAL'), this)
			},
			// 新增目标列
			onAddGoalCondButtonClicked: function() {
				let prevCondList = this.questGoalObject.condList
				let prevParams = this.questGoalObject.params
				let currID = this.questGoalObject.sn.split(':')[0]
				
				// 插入空值
				prevCondList.push('')
				prevParams.push('')
				
				// 更新本地缓存
				for (let i = 0; i < this.QuestGoals.length; i++) {
					if (this.QuestGoals[i].key == currID) {
						this.QuestGoals[i].value.condList = prevCondList.join(',')
						this.QuestGoals[i].value.params = prevParams.join(';')
					}
				}
				
				// 刷新页面以显示新增的条件列
				this.refreshDefaultValues()
			},
			
			// 点击【新增任务目标】弹窗中的【提交】按钮的点击事件
			onAddQuestGoal: function(e) {
				this.$refs['addQuestGoalForm'].validate((valid) => {
					if (valid) {
						// 检查提供的任务目标SN是否合法
						uni.request({
							url: msg.url(),
							method: 'GET',
							data: msg.get_table_data_by_sn(util.getCurrentUserToken(), this.QUESTGOAL, this.addQuestGoalForm.newQuestGoalId),
							success: res => {
								let dataWithGivenID = res.data['data']
								
								// 提供的ID在数据库中已有对应的数据
								if (dataWithGivenID != null) {
									this.$notify.error({
										title: '添加失败',
										message: '提供的任务目标ID已存在，请重试！'
									});
								}
								else {
									this.loadingInstance = this.$loading({
										 lock: true,
										 text: "新增记录中...",
										 spinner: 'el-icon-loading',
										 background: 'rgba(0, 0, 0, 0.7)'
									})
									// 定义添加的数据对象
									let newQuestGoal = {
										sn: this.addQuestGoalForm.newQuestGoalId,
										desc: this.addQuestGoalForm.newQuestGoalDesc,
										combinationType: 1,
										relationType: 0,
										condList: '',
										params: '',
										targetPos: '',
										lookAtPos: ''
									}
									
									// 标识数据过期
									this.shouldReloadQuestGoal = true

									// 更新本地QuestGoal缓存
									this.QuestGoals.push({
										key: this.addQuestGoalForm.newQuestGoalId,
										value: newQuestGoal,
									})
									
									// 向服务器提交新增的数据
									util.addDataField(this.QUESTGOAL, JSON.stringify(newQuestGoal), this, this.onFinishedAddingQuestGoal)
								}
							}
						})
					} else {
						return false
					}
				})
			},
			
			// 新增任务目标后的回调方法
			onFinishedAddingQuestGoal: function() {
				// 停止loading界面的显示
				this.loadingInstance.close()
				
				// 隐藏弹窗
				this.showAddQuestGoalDialog = false
				
				// 显示成功提示
				this.$notify.success({
					title: '添加成功',
					message: '添加成功，新增任务目标：' + this.addQuestGoalForm.newQuestGoalId
				});
			},
			
			// 点击【新增任务目标】弹窗中的【取消】按钮的点击事件
			onCancelAddQuestGoal: function() {
				this.showAddQuestGoalDialog = false
			},

			// 点击【新增任务目标】按钮的事件
			onClickAddQuestGoalButton: function(e) {
				this.showAddQuestGoalDialog = true
				
				// 找到当前最大的QuestGoal的ID
				let currMaxQuestGoalId = parseInt(this.questGoalObject.sn)
				for (let i = 0; i < this.QuestGoals.length; i++) {
					currMaxQuestGoalId = Math.max(currMaxQuestGoalId, parseInt(this.QuestGoals[i].key))
				}
				
				// 对当前最大的QuestGoalID自增1
				this.addQuestGoalForm.newQuestGoalId = currMaxQuestGoalId + 1
			},
			
			// 点击弹窗中的数据触发的事件
			onSelect: function(e) {},
		}, 
		props: ['tableRowData'],
		components: {
			textInput,
			rainbowDivider
		}
	}
</script>

<style>
  view {
  	line-height: 1.0;
  	font-size: 10px;
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
  
  .clearfix {
  	  font-size: 10upx;
  	  font-weight: bold;
	  line-height: 1.5;
  }
  
  .float {
	  position:fixed;
	  top: 18.0%;
	  right: 8.5%;
	  text-align: center;
	  box-shadow: 2px 2px 3px #999;
	  z-index: 10;
  }
  
  .divider {
	  margin-top: 10upx;
  }
</style>
