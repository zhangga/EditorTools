<template>
	<view>
		<!-- 任务属性 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务属性</span>
			</view>
			<el-form label-width="38upx">
				<el-form-item label="任务名称">
					<el-input 
					placeholder="请输入String类型名称(对应questName)" 
					v-model="inputQuestName" 
					id="questName" 
					@change="onFormItemModified($event)" 
					@click.native="updateCurrentFocusedItem"
					clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="任务描述">
					<el-input 
						placeholder="请输入String类型描述(对应questDescription)" 
						type="textarea" 
						:rows="2" 
						v-model="inputQuestDesc" 
						id="questDescription" 
						@change="onFormItemModified"
						@click.native="updateCurrentFocusedItem"
						clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="任务类型">
					<el-select 
						v-model="selectedQuestType" 
						size="medium" 
						id="questType" 
						@change="onFormItemModified"
						@click.native="updateCurrentFocusedItem">
						<el-option v-for="questType in questTypes" :key="questType" :value="questType"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="屏蔽剧情ID" id="banPlotId">
					<textInput :datas="Plot" placeholder='屏蔽剧情ID' :method='loadPlot' :select="onSelectBanPlotId" v-bind:value='selectedBanPlotID'>
					</textInput>
				</el-form-item>
				<el-form-item label="组队人数限制" id="teamMembers">
					<el-input-number 
						v-model="inputTeamMembers" 
						:min="0" 
						@change="onUpdateTeamMembers">
					</el-input-number>
				</el-form-item>
				<el-row>
					<el-col :span="5">
						<el-form-item label-width="0">
							<el-checkbox 
								v-model="selectedAcceptMirrorSceneID" 
								id="acceptMirrorSceneID"
								@change="onFormItemModified" 
								@click.native="updateCurrentFocusedItem">
								开启位面场景ID
							</el-checkbox>
						</el-form-item>
					</el-col>
					<el-col :span="5" :offset="1">
						<el-form-item label-width="0">
							<el-checkbox 
								v-model="selectedRepeatable" 
								id="repeatable"
								@change="onFormItemModified" 
								@click.native="updateCurrentFocusedItem">
								是否可重复完成
							</el-checkbox>
						</el-form-item>
					</el-col>
					<el-col :span="5" :offset="1">
						<el-form-item label-width="0">
							<el-checkbox 
								v-model="selectedHidden" 
								id="hidden"
								@change="onFormItemModified" 
								@click.native="updateCurrentFocusedItem">
								任务是否隐藏
							</el-checkbox>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
		</el-card>
		
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务关联</span>
			</view>
			<el-form label-width="38upx">
				<!-- <el-form-item label="前置任务ID" id="preId">
					<textInput :datas="taskNames" placeholder='前置任务ID' :method='getFormattedTaskNames' :select="onSelectPreId" v-bind:value='selectedPreID'>
					</textInput>
				</el-form-item> -->
				<el-form-item label="后接任务ID" id="postId">
					<textInput :datas="taskNames" placeholder='后接任务ID' :method='getFormattedTaskNames' :select="onSelectPostId" v-bind:value='selectedPostID'>
					</textInput>
				</el-form-item>
				<el-form-item label="屏蔽任务ID" id="banQuestId">
					<textInput :datas="taskNames" placeholder='屏蔽任务ID' :method='getFormattedTaskNames' :select="onSelectBanQuestId" v-bind:value='selectedBanQuestID'>
					</textInput>
				</el-form-item>
			</el-form>
		</el-card>
		
		<!-- 任务追踪相关 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务追踪</span>
			</view>
			<el-form label-width="55upx">
				<el-form-item label="任务追踪目标描述">
					<el-input 
						placeholder="请输入String类型的任务追踪目标描述" 
						type="textarea" 
						:rows="2" 
						v-model="inputConnectedTrackingAction" 
						id="connectedTrackingAction" 
						@change="onFormItemModified"
						@click.native="updateCurrentFocusedItem"
						clearable>
					</el-input>
				</el-form-item>
				<el-form-item label="完成任务后追踪描述">
					<el-input 
						placeholder="请输入String类型的完成任务后追踪描述" 
						type="textarea" 
						:rows="2" 
						v-model="inputFinishedTrackingDesc" 
						id="finishedTrackingDesc" 
						@change="onFormItemModified"
						@click.native="updateCurrentFocusedItem"
						clearable>
					</el-input>
				</el-form-item>
			</el-form>
		</el-card>
		
		<!-- 导航属性相关 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">导航属性</span>
			</view>
			<el-form label-width="48upx">
				<el-form-item label="寻路指引可见状态">
					<textInput :datas="navVisStatusOptions" placeholder='寻路指引可见状态' :method='getNavVisStatusOptions' 
					:select="onSelectNavVisStatus" v-bind:value='selectedNavVisStatus'>
					</textInput>
				</el-form-item>
				<el-form-item label="固定导航路径">
					<el-popover
						ref="fixedNavPath"
						placement="right"
						width="220"
						trigger="focus"
						v-model="isFixedNavPathPopoverVisible">
						<el-form>
							<el-form-item label="X坐标">
								<el-input-number v-model="fixedNavPathCoord.x" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item label="Y坐标">
								<el-input-number v-model="fixedNavPathCoord.y" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item label="Z坐标">
								<el-input-number v-model="fixedNavPathCoord.z" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item>
								<el-button type="primary" @click="onSubmitFixedNavPathCoord" size="mini">添加</el-button>
							</el-form-item>
						</el-form>
					</el-popover>
					<el-input 
						placeholder="请输入String类型的固定导航路径(格式:x1,y1,z1;x2,y2,z2)" 
						v-model="inputFixedNavPath" 
						id="fixedNavPath" 
						v-popover:fixedNavPath>
					</el-input>
				</el-form-item>
				<el-form-item label-width="0upx">
					<el-checkbox 
						v-model="selectedAutoSmoothCamera" 
						id="autoSmoothCamera"
						@change="onFormItemModified" 
						@click.native="updateCurrentFocusedItem">
						是否自动旋转相机
					</el-checkbox>
				</el-form-item>
			</el-form>
		</el-card>
		
		<!-- 用于触发组件之间的数据同步与更新 -->
		<span style="display:none"> {{tableRowData['questName']}} </span>
	</view>
</template>

<script>
	import textInput from '../component/textInput.vue'
	import config from '../../common/config.js'
	import util from '../../common/util.js'
	
	export default {
		data() {
			return {
				/* 任务属性卡片相关 */
				inputQuestName: '',
				inputQuestDesc: '',
				selectedQuestType: '',
				inputTeamMembers: '',
				selectedPostID: '',
				selectedBanQuestID: '',
				selectedBanPlotID: '',
				selectedRepeatable: false,
				selectedAcceptMirrorSceneID: false,
				selectedHidden: false,
				
				/* 任务追踪相关 */
				inputConnectedTrackingAction: '',
				inputFinishedTrackingDesc: '',
				
				/* 导航属性相关 */
				inputFixedNavPath: null,
				fixedNavPathCoord: {
					x: '',
					y: '',
					z: ''
				},
				isFixedNavPathPopoverVisible: false,
				selectedNavVisStatus: '',
				navVisStatusOptions: [{
					key: '1',
					value: '1:都不可见'
				}, {
					key: '2',
					value: '2:真身可见'
				}, {
					key: '3',
					value: '3:灵魂可见'
				}, {
					key: '4',
					value: '4:都可见'
				}],
				selectedAutoSmoothCamera: '',
				
				/* 表数据相关 */
				taskNames: [],
				Plot: [],
				
				/* 全局相关 */
				hasSetDefaultValue: false,
				hasInitializedFixedNavPath: false,
				hasSetPlotRelatedDefaultValues: false,
				hasSetTaskRelatedDefaultValues: false,
				prevTableRowData: null,
				currentClickedFormItem: '',
				hasSwitchedQuest: false
			};
		},
		props: ['tableRowData', 'questTypes', 'allTableData', 'currTableName'],
		watch: {
			inputFixedNavPath: function () {
				// 1. 如果是切换任务导致的更新，不触发调用submit方法
				// 2. 如果是首次更新fixedNavPath值（首次进入该页面），也不触发调用submit方法
				if (!this.hasSwitchedQuest && this.hasInitializedFixedNavPath) {
					// 当输入框中内容变化时，触发调用该方法
					this.onSubmitFixedNavPath(this.inputFixedNavPath)
				}
				else {
					this.hasInitializedFixedNavPath = true
					this.hasSwitchedQuest = false
				}
			}
		},
		mounted: function() {
			this.getFormattedTaskNames()
			this.loadPlot()
		},
		updated: function() {	
			// 如果是用户输入触发的更新，不刷新默认值
			if (this.hasSetDefaultValue) {
				// 如果是由于切换任务导致的更新，刷新默认值
				if (this.prevTableRowData != this.tableRowData) {
					// 如果是首次进入该页面并获取数据，则不需要更新hasSwitchedQuest为true
					if (this.prevTableRowData != null) {
						this.hasSwitchedQuest = true
					}
					this.refreshDefaultValues()
					this.prevTableRowData = this.tableRowData
					
				}
			}
			
			// 数据过期，重新获取所有的任务名
			if (util.isQuestDataExpired) {
				this.getFormattedTaskNames()
			}
		},
		methods: {
			refreshDefaultValues: function() {
				this.inputQuestName = this.tableRowData['questName']
				this.inputQuestDesc = this.tableRowData['questDescription']
				this.selectedQuestType = this.tableRowData['questType'] == null ? null : this.questTypes[parseInt(this.tableRowData['questType'], 10) - 1]
				this.selectedBanPlotID = this.tableRowData['banPlotId'] == null ? null : this.getPlotName(this.tableRowData['banPlotId'])
				this.inputTeamMembers = this.tableRowData['teamMembers']
				
				this.selectedPostID = this.tableRowData['postId'] == null ? null : this.getTaskName(this.tableRowData['postId'])
				this.selectedBanQuestID = this.tableRowData['banQuestId'] == null ? null : this.getTaskName(this.tableRowData['banQuestId'])
				
				this.inputConnectedTrackingAction = this.tableRowData['connectedTrackingAction']
				this.inputFinishedTrackingDesc = this.tableRowData['finishedTrackingDesc']
				
				this.selectedNavVisStatus = this.tableRowData['navVisStatus']
				this.inputFixedNavPath = this.tableRowData['fixedNavPath']

				this.selectedRepeatable = this.tableRowData['repeatable'] == null ? false : (this.tableRowData['repeatable'] == "TRUE")
				this.selectedAcceptMirrorSceneID = this.tableRowData['acceptMirrorSceneID'] == null ? false : (this.tableRowData['acceptMirrorSceneID'] == "1")
				this.selectedHidden = this.tableRowData['hidden'] == null ? false : (this.tableRowData['hidden'] == "TRUE")
				this.selectedAutoSmoothCamera = this.tableRowData['autoSmoothCamera'] == null ? false : (this.tableRowData['autoSmoothCamera'] == "TRUE")
				
				this.hasSetDefaultValue = true;
			},
			
			// 加载对话表
			loadPlot: function() {
				util.loadTableData(this.Plot, 'PLOT', 'sn', '1', this.onFinishedLoadingPlot, this)
			},
			
			// 加载对话表后的回调方法
			onFinishedLoadingPlot: function() {
				if (!this.hasSetPlotRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetPlotRelatedDefaultValues = true;
				}
			},
			
			// 初始化寻路指引可见状态的选项
			getNavVisStatusOptions: function() {
				return this.navVisStatusOptions
			},
			
			// 初始化任务名列表
			getFormattedTaskNames: function() {
				// 复用此前的缓存
				if (this.taskNames.length > 0 && !util.isQuestDataExpired) {
					return
				}
				
				let mergedTableData = []
			
				for (let i = 0; i < this.allTableData.length; i++) {
					mergedTableData.push(...this.allTableData[i]['children'])
				}
				
				for (let i = 0; i < mergedTableData.length; i++) {
					this.taskNames[i] = {
						key: mergedTableData[i]['id'],
						value: mergedTableData[i]['text']
					}
				}
				
				// 还原flag
				util.isQuestDataExpired = false
				
				if (!this.hasSetTaskRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetTaskRelatedDefaultValues = true
				}
			},
			
			// 获取taskID对应的task描述
			getTaskName: function(taskID) {
				if (taskID == '') {
					return taskID
				}
				for (let i = 0; i < this.taskNames.length; i++) {
					if (this.taskNames[i].key == taskID) {
						return this.taskNames[i].value
					}
				}
				return taskID
			},
			
			// 获取plotID对应的plot描述
			getPlotName: function(plotID) {
				if (plotID == '') {
					return plotID
				}
				for (let i = 0; i < this.Plot.length; i++) {
					return this.Plot[i].value
				}
				return plotID
			},
			
			// 表单组件内容发生改变时调用
			onFormItemModified: function(updatedValue) {
				let currItem = this.currentClickedFormItem
				switch (currItem) {
					case 'questName':
					case 'questDescription':
					case 'connectedTrackingAction':
					case 'finishedTrackingDesc':
						console.log("向" + currItem + "提交值：" + updatedValue)
						util.updateDataField(this.currTableName, this.tableRowData['sn'], currItem, updatedValue, this.$store.state.verNum.get(this.currTableName), this)
						break
					case 'questType':
						console.log("向" + currItem + "提交值：" + parseInt(this.questTypes.indexOf(updatedValue) + 1))
						util.updateDataField(this.currTableName, this.tableRowData['sn'], currItem, parseInt(this.questTypes.indexOf(updatedValue) + 1), this.$store.state.verNum.get(this.currTableName), this)
						break
					case 'hidden':
					case 'repeatable':
					case 'autoSmoothCamera':
						console.log("向" + currItem + "提交值：" + (updatedValue ? "TRUE" : "FALSE"))
						util.updateDataField(this.currTableName, this.tableRowData['sn'], currItem, (updatedValue ? "TRUE" : "FALSE"), this.$store.state.verNum.get(this.currTableName), this)
						break
					case 'acceptMirrorSceneID':
						console.log("向" + currItem + "提交值：" + (updatedValue ? "1" : "0"))
						util.updateDataField(this.currTableName, this.tableRowData['sn'], currItem, (updatedValue ? "1" : "0"), this.$store.state.verNum.get(this.currTableName), this)
						break
				}
			},
			
			// 更新当前点击表单组件的id
			updateCurrentFocusedItem: function(event) {
				// 如果点击的是checkbox类型的组件（特殊处理）
				if (event.target.className.indexOf("el-checkbox") != -1) {
					let currComponent = event.target
					// 逐级向上母组件的ID
					while (currComponent.id == '') {
						currComponent = currComponent.parentNode
					}
					this.currentClickedFormItem = currComponent.id
				}
				else {
					this.currentClickedFormItem = event.target.id
				}
				console.log("点击了表单组件：" + this.currentClickedFormItem)
			},
			
			// popover类型的输入框内容更新事件处理
			onSubmitFixedNavPath: function(updatedValue) {
				console.log("向fixedNavPath提交值：" + updatedValue)
				util.updateDataField(this.currTableName, this.tableRowData['sn'], 'fixedNavPath', updatedValue, this.$store.state.verNum.get(this.currTableName), this)
			},
			
			// el-input-number类型的组件较为特殊，原生的click方法会在@change方法之后被调用，导致方法的执行顺序出错
			onUpdateTeamMembers: function(updatedValue) {
				console.log("向teamMembers提交值：" + updatedValue)
				util.updateDataField(this.currTableName, this.tableRowData['sn'], 'teamMembers', updatedValue, this.$store.state.verNum.get(this.currTableName), this)
			},
			
			// TextInput类型表单组件的事件
			onSelectPostId: function(item) {
				this.selectedPostID = item.value
				console.log("向postID提交值：" + this.selectedPostID.split(':')[0])
				util.updateDataField(this.currTableName, this.tableRowData['sn'], 'postId', this.selectedPostID.split(':')[0], this.$store.state.verNum.get(this.currTableName), this)
			},
			onSelectBanQuestId: function(item) {
				this.selectedBanQuestID = item.value
				console.log("向banQuestID提交值：" + this.selectedBanQuestID.split(':')[0])
				util.updateDataField(this.currTableName, this.tableRowData['sn'], 'banQuestId', this.selectedBanQuestID.split(':')[0], this.$store.state.verNum.get(this.currTableName), this)
			},
			onSelectBanPlotId: function(item) {
				this.selectedBanPlotID = item.value
				console.log("向banPlotID提交值：" + this.selectedBanPlotID.split(':')[0])
				util.updateDataField(this.currTableName, this.tableRowData['sn'], 'banPlotId', this.selectedBanPlotID.split(':')[0], this.$store.state.verNum.get(this.currTableName), this)
			},
			onSelectNavVisStatus: function(item) {
				console.log(item)
				let value = ""
				
				if (item.key != null) {
					value = item.key
				}
				
				this.selectedNavVisStatus = item.value
				console.log("向navVisStatus提交值：" + value)
				util.updateDataField(this.currTableName, this.tableRowData['sn'], 'navVisStatus', value, this.$store.state.verNum.get(this.currTableName), this)
			},
			
			// 点击弹窗中的提交按钮后的响应事件
			onSubmitFixedNavPathCoord: function() {
				if (this.inputFixedNavPath != null && this.inputFixedNavPath.length > 0) {
					this.inputFixedNavPath = this.inputFixedNavPath + ';'
				}
				
				if (this.inputFixedNavPath == null) {
					this.inputFixedNavPath = ''
				}
				
				let coords = this.fixedNavPathCoord
				this.inputFixedNavPath = String(this.inputFixedNavPath + coords.x + ',' + coords.y + ',' + coords.z)
				
				// 隐藏弹窗
				this.isFixedNavPathPopoverVisible = false
			}
		},
		components: {
			textInput
		}
	}
</script>

<style>
  view {
	  line-height: 1.0;
	  font-size: 10px;
  }
  .box-card {
	  width: 300upx;
	  margin-bottom: 10upx;
  }
  
  .el-input {
	  font-family: "PingFang SC"
  }
  
  .text {
	  font-size: 6upx;
	  font-family: "PingFang SC"
  }
  
  .clearfix {
	  font-size: 10upx;
	  font-weight: bold;
	  line-height: 1.5;
  }
</style>
