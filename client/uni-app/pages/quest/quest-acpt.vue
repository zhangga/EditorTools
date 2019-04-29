<template>
	<view style="flex-direction: column;">
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务接受与结束</span>
			</view>
			<el-form :label-position="labelPosition" label-width="42upx">
				<el-form-item label="接受NPC">
					<textInput :datas="NPC" placeholder='接受NPC' :method='loadNpc' :select="setStartNPC" v-bind:value='startNpc' id="startNPC" >
					</textInput>
				</el-form-item>

				<el-form-item label="结束NPC">
					<textInput :datas="NPC" placeholder='结束NPC' :method='loadNpc' v-bind:value="endNpc" :select="setEndNPC" id="endNPC" >
					</textInput>
				</el-form-item>

				<el-form-item label="接受前对话">
					<textInput :datas="Plot" placeholder='接受前对话' :method='loadPlot' v-bind:value="beforeAcceptPlot" :select="setBeforeAcceptPlot" 
					 id="beforeAcceptPlot">
					</textInput>
				</el-form-item>

				<el-form-item label="接受后对话">
					<textInput :datas="Plot" placeholder='接受后对话' :method='loadPlot' :select="setAfterAcceptPlot" v-bind:value='afterAcceptPlot'
					 id="afterAcceptPlot">
					</textInput>
				</el-form-item>

				<el-form-item label="结束前对话">
					<textInput :datas="Plot" placeholder='结束前对话' :method='loadPlot' :select="setBeforeEndPlot" v-bind:value='beforeEndPlot'
					 id="beforeEndPlot">
					</textInput>
				</el-form-item>

				<el-form-item label="结束后对话">
					<textInput :datas="Plot" placeholder='结束后对话' :method='loadPlot' :select="setAfterEndPlot" v-bind:value='afterEndPlot' id="afterEndPlot">
					</textInput>
				</el-form-item>
				
				<el-form-item label="接取任务选项">
					<textInput :datas="Plot" placeholder='接取任务选项' :method='loadPlot' :select="onSetInitPlot" v-bind:value='initPlot' id="initPlot">
					</textInput>
				</el-form-item>
				
				<el-form-item label="接取条件">
					<textInput :datas="Condition" placeholder='接取条件' :method='loadCondition' :select="onSetCondition" v-bind:value='condition' 
					v-bind:emptyValue='emptyConditionValue' id="condition">
					</textInput>
				</el-form-item>
				
				<el-form-item label="不满足条件失败">
					<textInput :datas="Condition" placeholder='不满足条件失败' :method='loadCondition' :select="onSetFailCond" v-bind:value='failCond' id="failCond">
					</textInput>
				</el-form-item>
				
				<el-form-item label="接受任务道具">
					<textInput :datas="Item" placeholder='接受任务道具' :method='loadItem' :select="onSetAcceptItem" v-bind:value='acceptItem' id="acceptItem"></textInput>
				</el-form-item>
				
				<el-form-item label="接受任务后行为">
					<el-col :span="5">
						<el-input placeholder="SN用逗号分隔" v-model="acceptAct" id="acceptAct"></el-input>
					</el-col>
					<el-col :span="5" :offset="1">
						<el-popover placement="right" width="400" trigger="click">
							<el-table :data="Action" height="250" width="400" @cell-click="onAddAcceptAct">
								<el-table-column width="100" property="key" label="SN"></el-table-column>
								<el-table-column width="300" property="value" label="描述"></el-table-column>
							</el-table>
							<el-col :offset="8" style="margin-top: 10upx">
								<span>提示：点击表格行可直接添加对应的任务</span>
							</el-col>
							<el-button type="info" icon="el-icon-search" @change="showAction = true" slot="reference">查询Action</el-button>
						</el-popover>
					</el-col>
				</el-form-item>

				<el-form-item label-width="10upx">
					<el-checkbox v-model="showAcceptedEffect" @change="setShowAcceptedEffect">是否显示接受特效</el-checkbox>
				</el-form-item>

				<el-form-item label-width="10upx">
					<el-checkbox v-model="showFinishedEffect" @change="setShowFinishedEffect">是否显示完成任务特效</el-checkbox>
				</el-form-item>
				
				<el-form-item label-width="10upx">
					<el-checkbox v-model="canGiveUp" @change="onSetCanGiveUp">是否可以手动放弃任务</el-checkbox>
				</el-form-item>
				
				<el-form-item label-width="10upx">
					<el-checkbox v-model="showInAcceptableList" @change="onSetShowInAcceptableList">是否显示在可接列表中</el-checkbox>
				</el-form-item>
			</el-form>
		</el-card>

		<!-- 用于触发数据同步与更新 -->
		<span style="display:none"> {{tableRowData['questName']}} </span>
	</view>
</template>

<script>
	import textInput from '../component/textInput.vue'
	import msg from '../../common/msg.js'
	import util from '../../common/util.js'
	
	export default {
		data() {
			return {
				startNpc: '0',
				endNpc: '0',
				beforeAcceptPlot: '0',
				afterAcceptPlot: '0',
				beforeEndPlot: '0',
				afterEndPlot: '0',
				initPlot: '0',
				condition: '',
				showAcceptedEffect: false,
				showFinishedEffect: false,
				canGiveUp: false,
				showInAcceptableList: false,
				failCond: '',
				acceptItem: '',
				acceptAct: null,
				
				/* 表数据相关 */
				NPC: [],
				Plot: [],
				Condition: [],
				Item: [],
				Action: [],
				emptyConditionValue: '0',

				/* 全局相关 */
				hasSwitchedQuest: false,
				labelPosition: 'right',
				tablePlot: "PLOT",
				showAction: false,
				hasSetDefaultValue: false,
				hasSetNPCRelatedDefaultValues: false,
				hasSetPlotRelatedDefaultValues: false,
				hasSetConditionRelatedDefaultValues: false,
				hasSetItemRelatedDefaultValues: false,
				hasSetActionRelatedDefaultValues: false,
				hasInitializedAcceptAct: false,
				prevTableRowData: null,
			};
		},
		props: ['tableRowData'],
		watch: {
			acceptAct: function () {
				// 如果是切换任务导致的更新，不触发调用该方法
				if (!this.hasSwitchedQuest && this.hasInitializedAcceptAct) {
					// 当输入框中内容变化时，触发调用该方法
					this.onSetAcceptAct()
				} else {
					this.hasSwitchedQuest = false
					this.hasInitializedAcceptAct = true
				}
			}
		},
		mounted() {
			this.refreshDefaultValues()
			this.loadNpc()
			this.loadPlot()
			this.loadCondition()
			this.loadItem()
			this.loadAction()
		},
		updated() {
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
		},
		methods: {
			// 加载NPC表
			loadNpc: function() {
				util.loadTableData(this.NPC, 'NPC', 'sn', 'name', this.onFinishedLoadingNpc, this)
			},
			// 加载NPC表后的回调方法
			onFinishedLoadingNpc: function() {
				if (!this.hasSetNPCRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetNPCRelatedDefaultValues = true;
				}
			},
			// 加载对话表
			loadPlot: function() {
				console.log("LOAD PLOT!!!")
				console.log(this)
				util.loadTableData(this.Plot, 'PLOT', 'sn', '1', this.onFinishedLoadingPlot, this)
				console.log(this.hasSetPlotRelatedDefaultValues)
			},
			// 加载对话表后的回调方法
			onFinishedLoadingPlot: function() {
				console.log("进入回调方法！")
				if (!this.hasSetPlotRelatedDefaultValues) {
					console.log("触发重刷新！")
					this.refreshDefaultValues()
					this.hasSetPlotRelatedDefaultValues = true;
				}
			},
			// 加载条件表数据
			loadCondition: function() {
				util.loadTableData(this.Condition, 'CONDITION', 'sn', 'comment', this.onFinishedLoadingCondition, this)
			},
			// 加载条件表后的回调方法
			onFinishedLoadingCondition: function() {
				if (!this.hasSetConditionRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetConditionRelatedDefaultValues = true
				}
			},
			// 加载物品表
			loadItem: function() {
				util.loadTableData(this.Item, 'ITEM', 'sn', 'name', this.onFinishedLoadingItem, this)
			},
			// 加载物品表后的回调方法
			onFinishedLoadingItem: function() {
				if (!this.hasSetItemRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetItemRelatedDefaultValues = true
				}
			},
			// 加载Action表
			loadAction: function() {
				util.loadTableData(this.Action, 'ACTION', 'sn', 'comment', this.onFinishedLoadingAction, this)
			},
			// 加载Action表后的回调方法
			onFinishedLoadingAction: function() {
				if (!this.hasSetActionRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetActionRelatedDefaultValues = true
				}
			},
			setStartNPC: function(item) {
				this.startNpc = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'acceptNPC',this.startNpc.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setEndNPC: function(item) {
				this.endNpc = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'endNPC',this.endNpc.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setBeforeAcceptPlot: function(item) {
				this.beforeAcceptPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'beforeAcceptPlotId',this.beforeAcceptPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setAfterAcceptPlot: function(item) {
				this.afterAcceptPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'afterAcceptPlotId',this.afterAcceptPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setBeforeEndPlot: function(item) {
				this.beforeEndPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'beforeEndPlotId',this.beforeEndPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setAfterEndPlot: function(item) {
				this.afterEndPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'afterEndPlotId',this.afterEndPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			onSetInitPlot: function(item) {
				console.log("触发initPlot更新")
				this.initPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'initPlot', this.initPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			onSetCondition: function(item) {
				console.log("触发condition更新")
				this.condition = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'condition', this.condition.split(':')[0], this.$store.state.verNum, this)
				}
			},
			onSetFailCond: function(item) {
				console.log("触发failCond更新")
				this.failCond = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'failCond', this.failCond.split(':')[0], this.$store.state.verNum, this)
				}
			},
			onSetAcceptItem: function(item) {
				console.log("触发acceptItem更新")
				this.acceptItem = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'acceptItem', this.acceptItem.split(':')[0], this.$store.state.verNum, this)
				}
			},
			onAddAcceptAct: function(row) {
				if (this.acceptAct != null && this.acceptAct.length > 0) {
					this.acceptAct = this.acceptAct + ','
				}
				
				if (this.acceptAct == null) {
					this.acceptAct = ''
				}
				
				this.acceptAct = String(this.acceptAct + row.key)
			},
			onSetAcceptAct: function(item) {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'acceptAct', this.acceptAct, this.$store.state.verNum, this)
				}
			},
			setShowAcceptedEffect: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'showAcceptedEffect', this.showAcceptedEffect ? '1' : '0', this.$store.state.verNum, this)
				}
			},
			setShowFinishedEffect: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'showFinishedEffect', this.showFinishedEffect ? '1' : '0', this.$store.state.verNum, this)
				}
			},
			onSetCanGiveUp: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'canGiveUp', this.canGiveUp ? 'TRUE' : 'FALSE', this.$store.state.verNum, this)
				}
			},
			onSetShowInAcceptableList: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'showInAcceptableList', this.showInAcceptableList ? 'TRUE' : 'FALSE', this.$store.state.verNum, this)
				}
			},
			refreshDefaultValues: function() {
				this.startNpc = this.tableRowData['acceptNPC'] != null ? this.findNPC(this.tableRowData['acceptNPC']) : null
				this.endNpc = this.tableRowData['endNPC'] != null ? this.findNPC(this.tableRowData['endNPC']) : null
				this.beforeAcceptPlot = this.tableRowData['beforeAcceptPlotId'] != null ? this.findPlot(this.tableRowData['beforeAcceptPlotId']) : null
				this.afterAcceptPlot = this.tableRowData['afterAcceptPlotId'] != null ? this.findPlot(this.tableRowData['afterAcceptPlotId']) : null
				this.beforeEndPlot = this.tableRowData['beforeEndPlotId'] != null ? this.findPlot(this.tableRowData['beforeEndPlotId']) : null
				this.afterEndPlot = this.tableRowData['afterEndPlotId'] != null ? this.findPlot(this.tableRowData['afterEndPlotId']) : null
				this.initPlot = this.tableRowData['initPlot'] != null ? this.findPlot(this.tableRowData['initPlot']) : null
				this.condition = this.tableRowData['condition'] != null ? this.findCondition(this.tableRowData['condition']) : null
				this.failCond = this.tableRowData['failCond'] != null ? this.findCondition(this.tableRowData['failCond']) : null
				this.acceptItem = this.tableRowData['acceptItem'] != null ? this.findItem(this.tableRowData['acceptItem']) : null
				this.acceptAct = this.tableRowData['acceptAct']
				
				this.showAcceptedEffect = this.tableRowData['showAcceptedEffect'] != null ? this.tableRowData['showAcceptedEffect'] !== '0' : null
				this.showFinishedEffect = this.tableRowData['showFinishedEffect'] != null ? this.tableRowData['showFinishedEffect'] !== '0' : null
				this.canGiveUp = this.tableRowData['canGiveUp'] != null ? this.tableRowData['canGiveUp'] == 'TRUE' : null
				this.showInAcceptableList = this.tableRowData['showInAcceptableList'] != null ? this.tableRowData['showInAcceptableList'] == 'TRUE' : null
				
				this.hasSetDefaultValue = true
			},
			findNPC: function(NpcID) {
				if (NpcID == '' || this.NPC.length == 0) {
					return NpcID
				}
				for (let i = 0; i < this.NPC.length; i++) {
					if (this.NPC[i].key === NpcID) {
						return this.NPC[i].value
					}
				}
				return NpcID
			},
			findPlot: function(plotID) {
				if (plotID == '' || this.Plot.length == 0) {
					return plotID
				}
				for (let i = 0; i < this.Plot.length; i++) {
					if (this.Plot[i].key === plotID) {
						return this.Plot[i].value
					}
				}
				return plotID
			},
			findCondition: function(conditionID) {
				if (conditionID == '' || conditionID == '0' || this.Condition.length == 0) {
					return conditionID
				}
				for (let i = 0; i < this.Condition.length; i++) {
					if (this.Condition[i].key == conditionID) {
						return this.Condition[i].value
					}
				}
				return conditionID
			},
			findItem: function(itemID) {
				if (itemID == '' || this.Item.length == 0) {
					return itemID
				}
				for (let i = 0; i < this.Item.length; i++) {
					if (this.Item[i].key == itemID) {
						return this.Item[i].value
					}
				}
				
				return itemID
			},
		},
		onLoad() {

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

	.el-form {
		align: middle;
	}

	.el-form-item {
		height: 20upx;
	}
	
	.clearfix {
		font-size: 10upx;
		font-weight: bold;
		line-height: 1.5;
	}
</style>
