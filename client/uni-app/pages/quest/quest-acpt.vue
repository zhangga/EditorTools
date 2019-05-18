<template>
	<view>
		<el-tabs tab-position="right">
			<el-tab-pane label="任务接取条件">
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">角色状态</span>
					</view>
					<el-form :label-position="labelPosition" label-width="42upx">
						<el-row>
							<el-col :span="4">
								<el-form-item label="开启等级限制">
									<el-checkbox v-model="isQuestLevelLimitEnabled"></el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="10">
								<el-form-item label="最低等级">
									<el-input-number 
										v-model="minQuestLevel" 
										:min="0" 
										@change="onUpdateMinQuestLevel"
										:disabled="!isQuestLevelLimitEnabled">
									</el-input-number>
								</el-form-item>
							</el-col>
							<el-col :span="10">
								<el-form-item label="最高等级">
									<el-input-number 
										v-model="maxQuestLevel" 
										:min="0" 
										@change="onUpdateMaxQuestLevel"
										:disabled="!isQuestLevelLimitEnabled">
									</el-input-number>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<el-col :span="3">
								<el-form-item label="开启性别限制">
									<el-checkbox v-model="isQuestSexLimitEnabled"></el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="性别">
									<textInput :datas="Sex" placeholder='性别' :method='loadSex' :select="onSetQuestSexLimit" v-bind:value='sexLimit' 
										id="sexLimit" v-bind:disabled="!isQuestSexLimitEnabled"></textInput>
								</el-form-item>
							</el-col>
							
							<!-- <el-col :span="1" :offset="1">
								<el-divider direction="vertical" style="font-size: 20upx !important"></el-divider>
							</el-col> -->
							
							<el-col :span="3" :offset="1">
								<el-form-item label="开启职业限制">
									<el-checkbox v-model="isQuestOccupationLimitEnabled"></el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="职业">
									<textInput :datas="Occupation" placeholder='职业' :method='loadOccupation' :select="onSetQuestOccupationLimit" v-bind:value='occupationLimit' 
										id="occupationLimit" v-bind:disabled="!isQuestOccupationLimitEnabled"></textInput>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<el-col :span="3">
								<el-form-item label="开启种族限制">
									<el-checkbox v-model="isQuestRaceLimitEnabled"></el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="种族">
									<textInput :datas="Race" placeholder='种族' :method='loadRace' :select="onSetQuestRaceLimit" v-bind:value='raceLimit' 
										id="raceLimit" v-bind:disabled="!isQuestRaceLimitEnabled"></textInput>
								</el-form-item>
							</el-col>
							
							<!-- <el-col :span="1" :offset="1">
								<el-divider direction="vertical" style="font-size: 20upx"></el-divider>
							</el-col> -->
							
							<el-col :span="3" :offset="1">
								<el-form-item label="开启阵营限制">
									<el-checkbox v-model="isQuestForceLimitEnabled"></el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="8">
								<el-form-item label="阵营">
									<textInput :datas="Force" placeholder='阵营' :method='loadForce' :select="onSetQuestForceLimit" v-bind:value='forceLimit' 
										id="forceLimit" v-bind:disabled="!isQuestForceLimitEnabled"></textInput>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</el-card>
				
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">任务和区域</span>
					</view>
					
					<el-form :label-position="labelPosition" label-width="42upx">
						<el-row>
							<el-col :span="4">
								<el-form-item label="任务条件">
									<el-checkbox v-model="isQuestLimitEnabled"></el-checkbox>
								</el-form-item>
							</el-col>
							
							<el-col :span="9">
								<el-form-item label="任务类型">
									<el-select 
										v-model="selectedQuestType" 
										id="questType" 
										@change="onChangeQuestType"
										v-bind:disabled="!isQuestLimitEnabled">
										<el-option v-for="questType in questTypes" :key="questType.key" :value="questType.value"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
							
							<el-col :span="9">
								<el-form-item label="任务ID">
									<textInput :datas="TaskNames" placeholder='任务ID' :method='loadTaskNames' :select="onSetQuestLimit" v-bind:value='questLimit' 
										id="questLimit" v-bind:disabled="!isQuestLimitEnabled"></textInput>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<el-col :span="4">
								<el-form-item label="位置条件">
									<el-checkbox v-model="isPositionLimitEnabled"></el-checkbox>
								</el-form-item>
							</el-col>
							
							<el-col :span="9">
								<el-form-item label="位置类型">
									<el-select 
										v-model="selectedPositionType" 
										id="positionType" 
										@change="onChangePositionType"
										v-bind:disabled="!isPositionLimitEnabled">
										<el-option v-for="positionType in positionTypes" :key="positionType.key" :value="positionType.value"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
							
							<el-col :span="9">
								<el-form-item label="位置参数">
									<el-input placeholder="位置参数" v-model="positionParameter" @change="onChangePositionParameter" 
										v-bind:disabled="!isPositionLimitEnabled"></el-input>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</el-card>
				
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">时间条件</span>
					</view>
					<el-form>
						<el-form-item label="发放时间段" label-width="40upx">
							<el-date-picker
								v-model="selectedQuestAvailTime"
								type="datetimerange"
								range-separator="到"
								start-placeholder="开始时间"
								end-placeholder="结束时间">
							</el-date-picker>
						</el-form-item>
						<el-form-item label="发放间隔" label-width="40upx">
							<el-row>
								<el-col :span="9">
									<el-select v-model="selectedQuestAvailInterval" size="medium" id="questType" @change="onQuestAvaiIntervalChanged">
										<el-option v-for="option in questAvailIntervalOptions" :key="option.id" :value="option.id" :label="option.desc"></el-option>
									</el-select>
								</el-col>
								<el-col :span="9" v-if="selectedQuestAvailInterval == 3">
									<el-input placeholder="请填写具体间隔秒数(单位:秒)" v-model="inputQuestAvailIntervalSeconds" id="questAvailIntervalSecond"></el-input>
								</el-col>
							</el-row>
						</el-form-item>
						<el-form-item>
							<span class="text">【说明：发放时间段和发放时间间隔可以同时生效，也可单独生效。下拉内容含有每隔一天、每隔一星期、每隔一月、每隔一段时间。每隔一段时间需要策划填写后面的具体秒数。】</span>
						</el-form-item>
					</el-form>
				</el-card>
				
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">特殊条件</span>
					</view>
					
					<el-button type='primary' round style="position:relative; bottom: 28upx; left: 65%" @click="onClickedAddConditionType">新增条件列</el-button>
					<el-button type='warning' round style="position:relative; bottom: 28upx; left: 65%" @click="onClickedDeleteConditionType">删除条件列</el-button>

					<el-form :label-position="labelPosition" label-width="42upx">
						<el-row>
							<el-col :span="10">
								<el-form-item label="条件ID">
									<textInput :datas="ConditionNames" placeholder='条件ID' :method='loadCondition' :select="onSetCondition" v-bind:value='conditionObject.id' 
										v-bind:emptyValue='emptyConditionValue' id="conditionID"></textInput>
								</el-form-item>
							</el-col>
							<el-col :span="10">
								<el-form-item label="备注">
									<el-input placeholder="备注" v-model="conditionObject.comment" @change="onUpdateConditionComment" :disabled="!hasValidCondition"></el-input>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<el-col :span="10">
								<el-form-item label="组合类型">
									<el-select v-model="conditionObject.combinationType" @change="onUpdateCombinationType" :disabled="!hasValidCondition">
										<el-option v-for="type in combinationTypes" :value="type.key" :label="type.value" :key="type.key"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
							<el-col :span="10">
								<el-form-item label="关系类型">
									<el-select v-model="conditionObject.relationType" @change="onUpdateRelationType" :disabled="!hasValidCondition">
										<el-option v-for="(type, index) in relationTypes" :value="index" :label="type" :key="index"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
						</el-row>
						
						<rainbow-divider v-if="conditionObject.condList.length > 0"></rainbow-divider>
						
						<el-row v-for="(cond, index) in conditionObject.condList" :value="cond" :label="cond" :key="index">
							<el-col :span="10">
								<el-form-item label="条件类型ID">
									<el-autocomplete placeholder="条件类型(ConditionType)ID"
										v-model="conditionObject.condList[index]" 
										:fetch-suggestions="queryConditionTypes"  @select="onSetConditionType" :id="index" @click.native="onSetConditionClicked">
									</el-autocomplete>
								</el-form-item>
							</el-col>
							<el-col :span="10">
								<el-form-item label="条件参数">
									<el-input placeholder="条件参数" v-model="conditionObject.params[index]" @change="onSetConditionParam"></el-input>
								</el-form-item>
							</el-col>
							<el-col :span="1" :offset="1">
								<el-collapse-transition>
									<el-button type="danger" icon="el-icon-minus" circle size="mini" style="margin-top: 20%; margin-left: -20%" 
										v-show="showDeleteConditionTypeButtons" @click.native="onClickDeleteEachConditionType" :id="index"></el-button>
								</el-collapse-transition>
							</el-col>
						</el-row>
					</el-form>
				</el-card>
				
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">其他</span>
					</view>
					<el-form :label-position="labelPosition" label-width="42upx">
						<el-row>
							<el-col :span="10">
								<el-form-item label="不满足条件失败">
									<textInput :datas="ConditionNames" placeholder='不满足条件失败' :method='loadCondition' :select="onSetFailCond" v-bind:value='failCond' id="failCond">
									</textInput>
								</el-form-item>
							</el-col>
						
							<el-col :span="10" :offset="1">
								<el-form-item label="接受任务道具">
									<textInput :datas="Item" placeholder='接受任务道具' :method='loadItem' :select="onSetAcceptItem" v-bind:value='acceptItem' id="acceptItem"></textInput>
								</el-form-item>
							</el-col>
						</el-row>
						
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
				
						<el-row>
							<el-col :span="10">
								<el-form-item label-width="10upx">
									<el-checkbox v-model="showAcceptedEffect" @change="onSetShowAcceptedEffect">是否显示接受特效</el-checkbox>
								</el-form-item>
							</el-col>
										
							<el-col :span="10" :offset="1">
								<el-form-item label-width="10upx">
									<el-checkbox v-model="showFinishedEffect" @change="onSetShowFinishedEffect">是否显示完成任务特效</el-checkbox>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<el-col :span="10">
								<el-form-item label-width="10upx">
									<el-checkbox v-model="canGiveUp" @change="onSetCanGiveUp">是否可以手动放弃任务</el-checkbox>
								</el-form-item>
							</el-col>
										
							<el-col :span="10" :offset="1">
								<el-form-item label-width="10upx">
									<el-checkbox v-model="showInAcceptableList" @change="onSetShowInAcceptableList">是否显示在可接列表中</el-checkbox>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</el-card>
			</el-tab-pane>
			<el-tab-pane label="任务接取方式">
				<el-form label-width="42upx">
					<el-form-item label="任务接取方式">
						<el-radio-group v-model="questAcceptManner" @input="onQuestAcceptMannerChanged">
							<el-radio-button v-for="manner in questAcceptManners" :label="manner.value" :key="manner.key" :value="manner.key"></el-radio-button>
						</el-radio-group>
					</el-form-item>
				</el-form>
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">NPC对话</span>
					</view>
					<el-form :label-position="labelPosition" label-width="46upx">
						<el-row>
							<el-col :span="10">	
								<el-form-item label="接取任务对话ID">
									<textInput :datas="Plot" placeholder='接取任务对话' :method='loadPlot' v-bind:value="beforeAcceptPlot" :select="onSetBeforeAcceptPlot" 
									 id="beforeAcceptPlot">
									</textInput>
								</el-form-item>
							</el-col>
							<el-col :span="10" :offset="1">
								<el-form-item label="任务进行中对话ID">
									<textInput :datas="Plot" placeholder='任务进行中对话' :method='loadPlot' :select="onSetAfterAcceptPlot" v-bind:value='afterAcceptPlot'
									 id="afterAcceptPlot">
									</textInput>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<el-col :span="10">	
								<el-form-item label="完成任务对话ID">
									<textInput :datas="Plot" placeholder='完成任务对话' :method='loadPlot' :select="onSetBeforeEndPlot" v-bind:value='beforeEndPlot'
									 id="beforeEndPlot">
									</textInput>
								</el-form-item>
							</el-col>
							<el-col :span="10" :offset="1">
								<el-form-item label="任务完成后对话ID">
									<textInput :datas="Plot" placeholder='任务完成后对话' :method='loadPlot' :select="onSetAfterEndPlot" v-bind:value='afterEndPlot' id="afterEndPlot">
									</textInput>
								</el-form-item>
							</el-col>
						</el-row>
						
						<el-row>
							<el-col :span="10">
								<el-form-item label="接取任务NPC ID" v-if="findQuestAcceptMannerIndex(questAcceptManner)==2">
									<textInput :datas="NPC" placeholder='接取任务NPC' :method='loadNPC' :select="onSetStartNPC" v-bind:value='startNpc' id="startNPC" >
									</textInput>
								</el-form-item>
							</el-col>
										
							<el-col :span="10" :offset="1">
								<el-form-item label="完成任务NPC ID">
									<textInput :datas="NPC" placeholder='完成任务NPC' :method='loadNPC' v-bind:value="endNpc" :select="onSetEndNPC" id="endNPC" >
									</textInput>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</el-card>
				
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">喊话</span>
					</view>
					<el-form :label-position="labelPosition" label-width="46upx">
						<el-row>
							<el-form-item label="喊话条件">
								<el-select v-model="callingCondition" placeholder="喊话条件" @change="onSelectCallingCondition">
									<el-option v-for="condition in callingConditions" :label="condition.value" :key="condition.key" :value="condition.key"></el-option>
								</el-select>
							</el-form-item>
						</el-row>
						<el-row>
							<el-col :span="10">	
								<el-form-item label="喊话NPC ID">
									<textInput :datas="NPC" placeholder="喊话NPC ID, 0为玩家" :method="loadNPC" v-bind:value="callingNPC" :select="onSetCallingNPC" 
										id="callingNPC">
									</textInput>
								</el-form-item>
							</el-col>
							<el-col :span="10" :offset="1">
								<el-form-item label="对话ID">
									<textInput :datas="Plot" placeholder="对话ID" :method="loadPlot" v-bind:value="callingPlot" :select="onSetCallingPlot" 
										id="callingPlot">
									</textInput>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</el-card>
				
			</el-tab-pane>
			<el-tab-pane label="任务失败条件">
				<el-card class="box-card">
					<view slot="header" class="clearfix">
						<span class="header">任务失败条件</span>
					</view>
					<el-form :label-position="labelPosition" label-width="10upx">
						<el-row>
							<el-col :span="4">
								<el-form-item label="">
									<el-checkbox v-model="canPerishCauseFailure">角色失败死亡</el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="4" :offset="1">
								<el-form-item label="">
									<el-checkbox v-model="canGoOfflineCauseFailure">下线失败</el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="4" :offset="1">
								<el-form-item label="">
									<el-checkbox v-model="canTimeoutCauseFailure">超时失败</el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="6" :offset="0">
								<el-form-item label="">
									<el-input-number :disabled="!canTimeoutCauseFailure" placeholder="秒"></el-input-number>
								</el-form-item>
							</el-col>
						</el-row>
						<el-row>
							<el-col :span="4">
								<el-form-item label="">
									<el-checkbox v-model="canOutOfAreaCauseFailure">不在区域内</el-checkbox>
								</el-form-item>
							</el-col>
							<el-col :span="6" :offset="1">
								<el-form-item label="">
									<el-select v-model="selectedAreaType" placeholder="区域类型" @change="onSelectAreaType" :disabled="!canOutOfAreaCauseFailure">
										<el-option v-for="type in areaTypes" :key="type.key" :label="type.value" :value="type.key"></el-option>
									</el-select>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</el-card>
			</el-tab-pane>
		</el-tabs>
		<!-- 用于触发数据同步与更新 -->
		<span style="display:none"> {{tableRowData['questName']}} </span>
	</view>
</template>

<script>
	import textInput from '../component/textInput.vue'
	import rainbowDivider from '../component/rainbowDivider.vue'
	import msg from '../../common/msg.js'
	import util from '../../common/util.js'
	import config from '../../common/config.js'
	
	export default {
		data() {
			return {
				/* ---------- 任务接取条件-【角色状态】相关 ---------- */
				isQuestLevelLimitEnabled: false,		// 等级
				isQuestSexLimitEnabled: false,			// 性别
				isQuestOccupationLimitEnabled: false,	// 职业
				isQuestRaceLimitEnabled: false,			// 种族
				isQuestForceLimitEnabled: false,		// 阵营
				minQuestLevel: '0',						// 最低等级限制
				maxQuestLevel: '0',						// 最高等级限制
				sexLimit: '',							// 性别限制
				occupationLimit: '',					// 职业限制
				raceLimit: '',							// 种族限制
				forceLimit: '',							// 阵营限制
				
				/* ---------- 任务接取条件-【任务和区域】相关 ---------- */
				isQuestLimitEnabled: false,
				isPositionLimitEnabled: false,
				questLimit: '',							// 任务条件
				positionParameter: '',
				selectedQuestType: '',
				selectedPositionType: '',
				questTypes: [{
					key: "1",
					value: "未接，互斥任务",
				}, {
					key: "2",
					value: "已接，关联任务",
				}, {
					key: "3",
					value: "完成，未提交，关联任务"
				}, {
					key: "4",
					value: "完成，已交，前置任务"
				}],
				positionTypes: [{
						key: "1",
						value: "区域",
					}, {
						key: "2",
						value: "位置",
					}, {
						key: "3",
						value: "场景"
					}
				],
				
				/* ---------- 任务接取条件-【时间条件】相关 ---------- */
				selectedQuestAvailTime: [],
				questAvailIntervalOptions: [
					{"id": 0, "desc":"每隔一天"}, 
					{"id": 1, "desc": "每隔一星期"}, 
					{"id":2, "desc": "每隔一个月"}, 
					{"id": 3, "desc": "每隔一段时间"}],
				selectedQuestAvailInterval: '',
				inputQuestAvailIntervalSeconds: '',
				isIntervalOfSecondSelected: false,
				
				/* ---------- 任务接取条件-【特殊条件】相关 ---------- */
				relationTypes: [],
				conditionObject: {
					id: '',							// 条件ID
					comment: '',					// 策划描述
					params: [],						// 参数
					condList: [],					// 条件列表
					relationType: '',				// 类型（0：单个，1：与关系，2：或关系）
					combinationType: '',			// 组合类型
				},
				combinationTypes: [{
					key: '1',
					value: "基础条件"
				}, {
					key: '2',
					value: "复合条件"
				}],
				
				/* ---------- 任务接取条件-【其他】相关 ---------- */
				showAcceptedEffect: false,
				showFinishedEffect: false,
				canGiveUp: false,
				showInAcceptableList: false,
				failCond: '',
				acceptItem: '',
				acceptAct: null,
				
				/* ---------- 任务接取方式-【任务接取方式】相关 ---------- */
				questAcceptManner: '',
				questAcceptManners: [{
					key: '1',
					value: '无'
				}, {
					key: '2',
					value: 'NPC对话接取'
				}, {
					key: '3',
					value: '自动接取'
				}, {
					key: '4',
					value: '手动接取'
				}],
				
				/* ---------- 任务接取方式-【NPC对话】相关 ---------- */
				startNpc: '0',
				endNpc: '0',
				beforeAcceptPlot: '0',
				afterAcceptPlot: '0',
				beforeEndPlot: '0',
				afterEndPlot: '0',
				
				/* ---------- 任务接取方式-【喊话】相关 ---------- */
				callingNPC: '',
				callingPlot: '',
				callingCondition: '',
				callingConditions: [{
					key: '1',
					value: '满足接取条件'
				}, {
					key: '2',
					value: '接到任务后'
				}, {
					key: '3',
					value: '满足完成条件'
				}, {
					key: '4',
					value: '完成任务后'
				}],
				
				/* ---------- 任务失败条件-【任务失败条件】相关 ---------- */
				canTimeoutCauseFailure: false,
				canPerishCauseFailure: false,
				canGoOfflineCauseFailure: false,
				canOutOfAreaCauseFailure: false,
				areaTypes: [{
					key: "1",
					value: "区域"
				}, {
					key: "2",
					value: "位置"
				}, {
					key: "3",
					value: "场景"
				}],
				selectedAreaType: '',
				
				/* ---------- 表数据相关 ---------- */
				Sex: [],
				Occupation: [],
				Race: [],
				Force: [],
				NPC: [],
				Plot: [],
				Condition: [],
				ConditionNames: [],
				Item: [],
				Action: [],
				TaskNames: [],
				ConditionTypes: null,
				ConditionTypeNames: [],
				emptyConditionValue: '0',
				
				/* ---------- 数据初始化相关 ---------- */
				hasSetDefaultValue: false,
				hasSetSexRelatedDefaultValues: false,
				hasSetOccupationRelatedDefaultValues: false,
				hasSetRaceRelatedDefaultValues: false,
				hasSetForceRelatedDefaultValues: false,
				hasSetNPCRelatedDefaultValues: false,
				hasSetPlotRelatedDefaultValues: false,
				hasSetConditionRelatedDefaultValues: false,
				hasSetItemRelatedDefaultValues: false,
				hasSetActionRelatedDefaultValues: false,
				hasSetTaskNameRelatedDefaultValues: false,
				hasSetConditionTypeRelatedDefaultValues: false,

				/* ---------- 全局相关 ---------- */
				hasSwitchedQuest: false,
				labelPosition: 'right',
				tablePlot: "PLOT",
				showAction: false,
				hasInitializedAcceptAct: false,
				prevTableRowData: null,
				currFocusedConditionType: '',
				shouldReloadCondition: false,
				showDeleteConditionTypeButtons: false,
				hasValidCondition: false,								// 标识当前的条件ID是否合法（不为0或者为空）
			};
		},
		props: ['tableRowData', 'allTableData'],
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
			// 配置初始值
			this.refreshDefaultValues()
			this.loadNPC()
			this.loadPlot()
			this.loadCondition()
			this.loadItem()
			this.loadAction()
			this.loadSex()
			this.loadTaskNames()
			this.loadConditionInfo()
		},
		updated() {
			// 更新条件ID合法标识
			this.hasValidCondition = (this.conditionObject.id != null && this.conditionObject.id != '0')
			
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
					
					// 数据刷新后需要重新检测是否有合法的Condition ID
					this.hasValidCondition = (this.conditionObject.id != null && this.conditionObject.id != '0')
					
					// 由于任务接取页面涉及到对Condition表的操作
					// 切换页面时需要获取新记录【条件ID】对应Condition记录的版本号（VerNum）
					if (this.hasValidCondition) {
						uni.request({
							url: msg.url(),
							method: 'GET',
							data: msg.get_version_number_by_sn(util.getCurrentUserToken(), 'CONDITION', this.conditionObject.id.split(':')[0] ),
							success: res => {
								this.$store.state.verNum.set('CONDITION', res.data.verNum)
							},
							fail: () => {
								this.$store.state.verNum.set('CONDITION', 'ignore')
							},
							complete: () => {
								console.log('获取CONDITION中记录为' + this.conditionObject.id.split(':')[0] + '的版本号为' + this.$store.state.verNum.get('CONDITION'))
							}
						})
					}
				}
			}
		},
		methods: {
			// 初始化表单选项
			refreshDefaultValues: function() {
				// TODO: 添加新增的表单项的初始化
				
				//初始化【任务接取条件-其他】表单项
				this.failCond = this.tableRowData['failCond'] != null ? this.findConditionName(this.tableRowData['failCond']) : null
				this.acceptItem = this.tableRowData['acceptItem'] != null ? this.findItem(this.tableRowData['acceptItem']) : null
				this.acceptAct = this.tableRowData['acceptAct']
				this.showAcceptedEffect = this.tableRowData['showAcceptedEffect'] != null ? this.tableRowData['showAcceptedEffect'] !== '0' : null
				this.showFinishedEffect = this.tableRowData['showFinishedEffect'] != null ? this.tableRowData['showFinishedEffect'] !== '0' : null
				this.canGiveUp = this.tableRowData['canGiveUp'] != null ? this.tableRowData['canGiveUp'] == 'TRUE' : null
				this.showInAcceptableList = this.tableRowData['showInAcceptableList'] != null ? this.tableRowData['showInAcceptableList'] == 'TRUE' : null
				
				// TODO: 初始化【任务接取条件-角色状态】表单项（当前暂无对应策划数据）
				
				// TODO: 初始化【任务接取条件-任务和区域】表单项（当前暂无对应策划数据）
				
				// TODO: 初始化【任务接取条件-时间条件】表单项（当前暂无对应策划数据）
				
				// 初始化【任务接取条件-特殊条件】表单项
				this.conditionObject.id = this.tableRowData['condition'] != null ? this.findConditionName(this.tableRowData['condition']) : null
				let conditionID = this.tableRowData['condition']
				let currConditionDetails = this.findCondition(conditionID)
				
				if (currConditionDetails != conditionID) {
					this.conditionObject.comment = currConditionDetails.comment
					this.conditionObject.params = currConditionDetails.params.split(';')
					this.conditionObject.condList = currConditionDetails.condList.split(',')
					this.conditionObject.combinationType = this.findCombinationType(currConditionDetails.combinationType)
					this.conditionObject.relationType = this.findRelationType(currConditionDetails.relationType)
				} else {
					this.conditionObject.comment = ''
					this.conditionObject.combinationType = ''
					this.conditionObject.relationType = ''
					this.conditionObject.params = ''
					this.conditionObject.condList = ''
				}
				
				// 初始化【任务接取方式-NPC对话】表单项
				this.startNpc = this.tableRowData['acceptNPC'] != null ? this.findNPC(this.tableRowData['acceptNPC']) : null
				this.endNpc = this.tableRowData['endNPC'] != null ? this.findNPC(this.tableRowData['endNPC']) : null
				this.beforeAcceptPlot = this.tableRowData['beforeAcceptPlotId'] != null ? this.findPlot(this.tableRowData['beforeAcceptPlotId']) : null
				this.afterAcceptPlot = this.tableRowData['afterAcceptPlotId'] != null ? this.findPlot(this.tableRowData['afterAcceptPlotId']) : null
				this.beforeEndPlot = this.tableRowData['beforeEndPlotId'] != null ? this.findPlot(this.tableRowData['beforeEndPlotId']) : null
				this.afterEndPlot = this.tableRowData['afterEndPlotId'] != null ? this.findPlot(this.tableRowData['afterEndPlotId']) : null
				
				// TODO: 初始化【任务接取方式-喊话】表单项（当前暂无对应策划数据）
				
				// TODO: 初始化【任务失败条件-任务失败条件】表单项（当前暂无对应策划数据）
				
				this.hasSetDefaultValue = true
			},
			
			/* -------------------- 监听事件 -------------------- */
			
			/* ---------- 任务接取方式相关 ---------- */
			onQuestAcceptMannerChanged: function(value) {
				let currQuestAcceptMannerIndex = ''
				
				currQuestAcceptMannerIndex = this.findQuestAcceptMannerIndex(value)
				
				// TODO: 写入表格
				console.log(currQuestAcceptMannerIndex)
			},
			onSetCallingNPC: function(item) {
				// TODO: 写入表格
				console.log(item.key)
			},
			onSetCallingPlot: function(item) {
				// TODO: 写入表格
				console.log(item.key)
			},
			onSelectCallingCondition: function(value) {
				// TODO: 写入表格
				console.log(value)
			},
			
			/* ---------- 角色状态相关 ---------- */
			onUpdateMaxQuestLevel: function(value) {
				this.maxQuestLevel = value
				if (this.tableRowData['sn'] != null) {
					// TODO: 写入表格
					console.log("UPDATE MAX QUEST LEVEL TO " + this.maxQuestLevel)
				}
			},
			onUpdateMinQuestLevel: function(value) {
				this.minQuestLevel = value
				if (this.tableRowData['sn'] != null) {
					// TODO: 写入表格
					console.log("UPDATE MIN QUEST LEVEL TO " + this.minQuestLevel)
				}
			},
			onSetQuestSexLimit: function(item) {
				this.sexLimit = item.value
				if (this.tableRowData['sn'] != null) {
					// TODO: 写入表格
					console.log("UPDATE SEX LIMIT TO " + this.sexLimit.split(':')[1])
				}
			},
			onSetQuestOccupationLimit: function(item) {
				this.occupationLimit = item.value
				if (this.tableRowData['sn'] != null) {
					// TODO: 写入表格
					console.log("UPDATE OCCUPATION LIMIT TO " + this.occupationLimit.split(":")[1])
				}
			},
			onSetQuestRaceLimit: function(item) {
				this.raceLimit = item.value
				if (this.tableRowData['sn'] != null) {
					// TODO: 写入表格
					console.log("UPDATE RACE LIMIT TO " + this.raceLimit.split(":")[1])
				}
			},
			onSetQuestForceLimit: function(item) {
				this.forceLimit = item.value
				if (this.tableRowData['sn'] != null) {
					// TODO: 写入表格
					console.log("UPDATE FORCE LIMIT TO " + this.forceLimit.split(":")[1])
				}
			},
			
			/* ----------- 任务与区域相关 ---------- */
			onSetQuestLimit: function(item) {
				this.questLimit = item.value
				if (this.tableRowData['sn'] != null) {
					// TODO: 写入表格
					console.log("UPDATE QUEST LIMIT TO " + this.forceLimit.split(":")[1])
				}
			},
			onChangeQuestType: function(value) {
				// TODO: 写入表格
				console.log('CHANGE QUEST TYPE TO ' + value)
			},
			onChangePositionType: function(value) {
				// TODO: 写入表格
				console.log('CHANGE LOCATION TYPE TO ' + value)
			},
			onChangePositionParameter: function(value) {
				// TODO: 写入表格
				console.log("CHANGE POSITION PARAMETER TO " + value)
			},
			
			/* ---------- 任务时间相关 ---------- */
			onQuestAvaiIntervalChanged: function(value) {
				// TODO: 写入表格
				this.selectedQuestAvailInterval = value
			},
			
			/* ---------- 特殊条件相关 ---------- */
			onUpdateConditionComment: function(value) {
				if (this.conditionObject.id != null) {
					this.conditionObject.comment = value
					
					// 重新加载Condition相关数据
					util.isConditionDataExpired = true
					
					// 更新本地缓存（Condition）内容
					let updatedComment = value
					let currID = this.conditionObject.id.split(':')[0]
					
					for (let i = 0; i < this.Condition.length; i++) {
						if (this.Condition[i].key == currID) {
							this.Condition[i].value.comment = updatedComment
							break
						}
					}
										
					// 重新加载Condition相关表单项的初始值
					this.shouldReloadCondition = true
					this.hasSetConditionRelatedDefaultValues = false
					
					// TODO: Condition表格暂时没有版本号
					util.updateDataField('CONDITION', currID, 'comment', updatedComment, this.$store.state.verNum.get('CONDITION'), this, this.loadCondition)
				}
			},
			// 更新【类型】表单项内容时触发的事件
			onUpdateRelationType: function(value) {
				if (this.conditionObject.id != null) {
					this.conditionObject.relationType = value
					
					// 如果【单个】选项被屏蔽，提交的序号需要 + 1
					if (this.relationTypes.length == 2) {
						value = parseInt(value) + 1
					}
					
					// 更新本地缓存（Condition）内容
					let updatedRelationType = value
					let currID = this.conditionObject.id.split(':')[0]
					
					for (let i = 0; i < this.Condition.length; i++) {
						if (this.Condition[i].key == currID) {
							this.Condition[i].value.relationType = updatedRelationType
							break
						}
					}
					
					util.updateDataField('CONDITION', currID, 'relationType', updatedRelationType, this.$store.state.verNum.get('CONDITION'), this)
				}
			},
			// 更新【组合类型】表单项内容时触发的事件
			onUpdateCombinationType: function(value) {
				if (this.conditionObject.id != null) {
					this.conditionObject.combinationType = value
					
					// 更新本地缓存（Condition）内容
					let updatedCombinationType = value
					let currID = this.conditionObject.id.split(':')[0]
					
					for (let i = 0; i < this.Condition.length; i++) {
						if (this.Condition[i].key == currID) {
							this.Condition[i].value.combinationType = updatedCombinationType
							break
						}
					}
					
					// TODO: Condition表格暂时没有版本号
					util.updateDataField('CONDITION', currID, 'combinationType', updatedCombinationType, "ignore", this)
				}
			},
			// 更新【条件ID】表单项内容时触发的事件
			onSetCondition: function(item) {
				let updatedValue = item.value.split(':')[0]
				this.tableRowData['condition'] = updatedValue
				
				if (this.tableRowData['sn'] != null) {
					// 更新数据后执行回调方法，刷新缓存数据
					util.updateDataField('QUEST', this.tableRowData['sn'], 'condition', updatedValue, this.$store.state.verNum.get('QUEST'), 
						this, this.refreshDefaultValues)
				}
			},
			// 更新【条件类型ID】表单项内容时触发的事件
			onSetConditionType: function(item) {
				if (this.conditionObject.id != null) {
					this.conditionObject.condList[this.currFocusedConditionType] = item.key
					
					// 更新本地缓存（Condition）内容
					let updatedCondList = this.conditionObject.condList.toString()
					let currID = this.conditionObject.id.split(':')[0]
					
					for (let i = 0; i < this.Condition.length; i++) {
						if (this.Condition[i].key == currID) {
							this.Condition[i].value.condList = updatedCondList
							break
						}
					}
					
					// TODO: Condition表格暂时没有版本号
					util.updateDataField('CONDITION', currID, 'condList', updatedCondList, "ignore", this)
				}
			},
			// 点击【条件类型ID】表单项时触发的点击事件，以确定当前点击的ConditionType对应的Index
			onSetConditionClicked: function(event) {
				this.currFocusedConditionType = event.target.id
			},
			// 更新【条件参数】表单项内容时触发的事件
			onSetConditionParam: function(value) {
				if (this.conditionObject.id != null) {
					this.conditionObject.params[this.currFocusedConditionType] = value
					
					// 更新本地缓存（Condition）内容
					let updatedParams = this.conditionObject.params.join(';')
					let currID = this.conditionObject.id.split(':')[0]
					
					for (let i = 0; i < this.Condition.length; i++) {
						if (this.Condition[i].key == currID) {
							this.Condition[i].value.params = updatedParams
							break
						}
					}
					
					// TODO: Condition表格暂时没有版本号
					util.updateDataField('CONDITION', currID, 'params', updatedParams, "ignore", this)
				}
			},
			// 点击【新增条件列】按钮时触发的事件
			onClickedAddConditionType: function() {
				let prevCondList = this.conditionObject.condList
				let prevParams = this.conditionObject.params
				let currID = this.conditionObject.id.split(':')[0]
				
				// 插入空值
				prevCondList.push('')
				prevParams.push('')
				
				// 更新本地缓存
				for (let i = 0; i < this.Condition.length; i++) {
					if (this.Condition[i].key == currID) {
						this.Condition[i].value.condList = prevCondList.join(',')
						this.Condition[i].value.params = prevParams.join(';')
					}
				}
				
				// 刷新页面以显示新增的条件列
				this.refreshDefaultValues()
			},
			// 点击【删除条件列】按钮时触发的事件
			onClickedDeleteConditionType: function() {
				this.showDeleteConditionTypeButtons = !this.showDeleteConditionTypeButtons
			},
			// 点击每个删除按钮时触发的事件
			onClickDeleteEachConditionType: function(event) {
				this.$confirm('确认要删除当前的条件列吗？', '提示', {
					confirmButtonText: '确认',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					let clickedItemIndex = event.target.parentNode.id
					let prevCondList = this.conditionObject.condList
					let prevParams = this.conditionObject.params
					let currID = this.conditionObject.id.split(':')[0]
					
					console.log("当前点击序号：" + clickedItemIndex)
					console.log(prevCondList)
					console.log(prevParams)
					
					// 删除指定条件
					prevCondList.splice(clickedItemIndex, 1)
					prevParams.splice(clickedItemIndex, 1)
					
					// 更新本地缓存
					for (let i = 0; i < this.Condition.length; i++) {
						if (this.Condition[i].key == currID) {
							this.Condition[i].value.condList = prevCondList.join(',')
							this.Condition[i].value.params = prevParams.join(';')
							
							this.conditionObject.params.join(';')
						}
					}
					
					// 持久化操作
					util.updateDataField('CONDITION', currID, 'condList', prevCondList.join(','), this.$store.state.verNum.get('CONDITION'), this)
					util.updateDataField('CONDITION', currID, 'params', prevParams.join(';'), this.$store.state.verNum.get('CONDITION'), this)
					
					// 刷新页面以使删除生效
					this.refreshDefaultValues()
					
					this.$message({
						type: 'success',
						message: '删除成功！'
					})
				}).catch(() => {})
			},
			/* ---------- 任务失败条件相关 ---------- */
			onSelectAreaType: function(value) {
				// TODO: 写入表格
				console.log(value)
			},
			
			onSetStartNPC: function(item) {
				this.startNpc = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'acceptNPC', this.startNpc.split(':')[0], this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetEndNPC: function(item) {
				this.endNpc = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'endNPC', this.endNpc.split(':')[0], this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetBeforeAcceptPlot: function(item) {
				this.beforeAcceptPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'beforeAcceptPlotId', this.beforeAcceptPlot.split(':')[0], this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetAfterAcceptPlot: function(item) {
				this.afterAcceptPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'afterAcceptPlotId',this.afterAcceptPlot.split(':')[0], this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetBeforeEndPlot: function(item) {
				this.beforeEndPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'beforeEndPlotId',this.beforeEndPlot.split(':')[0], this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetAfterEndPlot: function(item) {
				this.afterEndPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'afterEndPlotId',this.afterEndPlot.split(':')[0], this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetFailCond: function(item) {
				this.failCond = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'failCond', this.failCond.split(':')[0], 
						this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetAcceptItem: function(item) {
				this.acceptItem = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'acceptItem', this.acceptItem.split(':')[0], 
						this.$store.state.verNum.get('QUEST'), this)
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
					util.updateDataField('QUEST', this.tableRowData['sn'], 'acceptAct', this.acceptAct, 
						this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetShowAcceptedEffect: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'showAcceptedEffect', this.showAcceptedEffect ? '1' : '0', 
						this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetShowFinishedEffect: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'showFinishedEffect', this.showFinishedEffect ? '1' : '0', 
						this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetCanGiveUp: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'canGiveUp', this.canGiveUp ? 'TRUE' : 'FALSE', 
						this.$store.state.verNum.get('QUEST'), this)
				}
			},
			onSetShowInAcceptableList: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'showInAcceptableList', this.showInAcceptableList ? 'TRUE' : 'FALSE', 
						this.$store.state.verNum.get('QUEST'), this)
				}
			},


			/* -------------------- 通过ID找到对应表项的文字描述（用于数据初始化） -------------------- */
			
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
			findConditionName: function(conditionID) {
				// condition项为空时默认值为‘0’
				if (conditionID == '' || conditionID == '0' || this.ConditionNames.length == 0) {
					return conditionID
				}
				for (let i = 0; i < this.ConditionNames.length; i++) {
					if (this.ConditionNames[i].key == conditionID) {
						return this.ConditionNames[i].value
					}
				}
				return conditionID
			},
			findCondition: function(conditionID) {
				// condition项为空时默认值为‘0’
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
			findConditionType: function(conditionTypeID) {
				if (conditionTypeID == '' || this.ConditionTypeNames.length == 0) {
					return conditionTypeID
				}
				for (let i = 0; i < this.ConditionTypeNames.length; i++) {
					if (this.ConditionTypeNames[i].key == conditionTypeID) {
						return this.ConditionTypeNames[i].value
					}
				}
				return conditionTypeID
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
			findRelationType: function(type) {
				if (type == '') {
					return type
				}
				
				this.relationTypes = config.ExcelConfig().RelationType
				
				if (this.conditionObject.condList.length > 1) {
					// 策划配置出错，配置了多个条件，但关系类型配置为了【单个】
					if (type == 0) {
						this.$notify({
							title: '配置出错',
							message: '请检查Condition表格配置数据：【条件列表中配置了多个条件，但关系类型（relationType)配置为了【单个】】',
							type: 'warning'
						});
						
						return this.relationTypes[parseInt(type)]
					}
					
					// 删去【单个】选项
					this.relationTypes = this.relationTypes.slice(1, this.relationTypes.length)
					return this.relationTypes[parseInt(type) - 1]
				}
				
				return this.relationTypes[parseInt(type)]
			},
			findQuestAcceptMannerIndex: function(value) {
				if (value == '' || value == null) {
					return value
				}
				
				for (let i = 0; i < this.questAcceptManners.length; i++) {
					if (this.questAcceptManners[i].value == value) {
						return this.questAcceptManners[i].key
					}
				}
				
				return value
			},
			
			/* --------------------- 数据加载相关方法 --------------------- */
			
			/* ---------- 加载性别表 ---------- */
			loadSex: function() {
				util.loadTableData(this.Sex, 'SEX', 'sn', 'name', this.onFinishedLoadingSex, this)
			},
			onFinishedLoadingSex: function() {
				// TODO: 增加初始化逻辑
				if (!this.hasSetSexRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetSexRelatedDefaultValues = true;
				}
			},
			/* ---------- 加载职业表 ---------- */
			loadOccupation: function() {
				util.loadTableData(this.Occupation, 'OCCUPATION', 'sn', 'name', this.onFinishedLoadingOccupation, this)
			},
			onFinishedLoadingOccupation: function() {
				// TODO: 增加初始化逻辑
				if (!this.hasSetOccupationRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetOccupationRelatedDefaultValues = true;
				}
			},
			/* ---------- 加载种族表 ---------- */
			loadRace: function() {
				// TODO: 从表格读入数据
				this.Race = [{
					key: '1',
					value: '人类'
				}, {
					key: '2',
					value: '精灵'
				}, {
					key: '3',
					value: '暴族'
				}]
			},
			onFinishedLoadingRace: function() {
				// TODO: 增加初始化逻辑
				if (!this.hasSetRaceRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetRaceRelatedDefaultValues = true;
				}
			},
			/* ---------- 加载阵营表 ---------- */
			loadForce: function() {
				// TODO: 从表格读入数据
				this.Force = [{
					key: '1',
					value: '阵营1'
				}, {
					key: '2',
					value: '阵营2'
				}, {
					key: '3',
					value: '阵营3'
				}]
			},
			onFinishedLoadingForce: function() {
				// TODO: 增加初始化逻辑
				if (!this.hasSetForceRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetForceRelatedDefaultValues = true;
				}
			},
			
			/* ---------- 加载任务名列表 ---------- */
			loadTaskNames: function() {
				// 复用此前的缓存
				if (this.TaskNames.length > 0 && !util.isQuestDataExpired) {
					return
				}
				
				let mergedTableData = []
			
				for (let i = 0; i < this.allTableData.length; i++) {
					mergedTableData.push(...this.allTableData[i]['children'])
				}
				
				for (let i = 0; i < mergedTableData.length; i++) {
					this.TaskNames[i] = {
						key: mergedTableData[i]['id'],
						value: mergedTableData[i]['text']
					}
				}
				
				// 还原flag
				util.isQuestDataExpired = false
				
				// TODO: 删除不需要的重刷新
				if (!this.hasSetTaskNameRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetTaskNameRelatedDefaultValues = true
				}
			},
			
			/* ---------- 加载条件相关数据 ---------- */
			loadConditionInfo: function() {
				if (this.ConditionTypes != null && !util.isConditionDataExpired) {
					return
				}
				
				// 需要重置旧数据
				this.ConditionTypes = new Map()
				this.Condition = []
				
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_condition_info(util.getCurrentUserToken()),
					success: res => {
						// 初始化条件类型数据(ConditionType)
						let condTypes = res.data.conditionType
						for (let i = 0; i < condTypes.length; i++) {
							let condType = condTypes[i]
							let condTypeName = condType['sn'] + ':' + condType['描述']
							
							this.ConditionTypeNames.push({key: condType['sn'], value: condTypeName})
							this.ConditionTypes.set(condType['sn'], condType)
						}
						
						// 初始化条件数据(Condition)
						let conds = res.data.condition
						for (let i = 0; i < conds.length; i++) {
							this.Condition.push({key: conds[i]['sn'], value: conds[i]})
						}
						
						if (!this.hasSetConditionTypeRelatedDefaultValues) {
							this.refreshDefaultValues()
							this.hasSetConditionTypeRelatedDefaultValues = true
						}
					},
					fail: () => {
						// TODO: Error Handling
					},
					complete: () => {
						util.isConditionDataExpired = false
					}
				})
			},
			queryConditionTypes: function(queryString, cb) {
				let items = this.ConditionTypeNames;
				let results = queryString ? items.filter(this.createFilter(queryString)) : items;
				cb(results);
			},
			createFilter: function(queryString) {
				return (type) => {
					return (type.value.indexOf(queryString) !== -1);
				};
			},

			/* ---------- 加载NPC表 ---------- */  
			loadNPC: function() {
				util.loadTableData(this.NPC, 'NPC', 'sn', 'name', this.onFinishedLoadingNpc, this)
			},
			onFinishedLoadingNpc: function() {			// 加载NPC表后的回调方法
				if (!this.hasSetNPCRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetNPCRelatedDefaultValues = true;
				}
			},
			/* ---------- 加载对话表 ---------- */ 
			loadPlot: function() {
				util.loadTableData(this.Plot, 'PLOT', 'sn', '1', this.onFinishedLoadingPlot, this)
			},
			onFinishedLoadingPlot: function() {			// 加载对话表后的回调方法
				if (!this.hasSetPlotRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetPlotRelatedDefaultValues = true;
				}
			},
			/* ---------- 加载任务表数据 ---------- */
			loadCondition: function() {
				util.loadTableData(this.ConditionNames, 'CONDITION', 'sn', 'comment', this.onFinishedLoadingCondition, this, this.shouldReloadCondition)
				this.shouldReloadCondition = false
			},
			onFinishedLoadingCondition: function() {	// 加载条件表后的回调方法
				if (!this.hasSetConditionRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetConditionRelatedDefaultValues = true
				}
			},
			/* ---------- 加载物品表 ---------- */
			loadItem: function() {
				util.loadTableData(this.Item, 'ITEM', 'sn', 'name', this.onFinishedLoadingItem, this)
			},
			onFinishedLoadingItem: function() {			// 加载物品表后的回调方法
				if (!this.hasSetItemRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetItemRelatedDefaultValues = true
				}
			},
			/* ---------- 加载Action表 ---------- */
			loadAction: function() {
				util.loadTableData(this.Action, 'ACTION', 'sn', 'comment', this.onFinishedLoadingAction, this)
			},
			onFinishedLoadingAction: function() {		// 加载Action表后的回调方法
				if (!this.hasSetActionRelatedDefaultValues) {
					this.refreshDefaultValues()
					this.hasSetActionRelatedDefaultValues = true
				}
			},
			
			/* -------------------- 其他方法 -------------------- */
			getCondParamWithIndex: function(index) {
				return this.conditionObject.condList[index]
			}
		},
		onLoad() {

		},
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

	.box-card {
		  width: 300upx;
		  margin-bottom: 10upx;
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
