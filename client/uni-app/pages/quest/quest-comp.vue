<template>
	<view style="flex-direction: column;">
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">常规奖励</span>
			</view>
			
			<el-form ref="form" label-width="40upx">
				<el-form-item label="任务奖励">
					<textInput :datas="DropGroup" placeholder='任务奖励' :method='loadDropGroup' :select="setQuestReward" :value='questReward' id='questReward'>
					</textInput>
				</el-form-item>
							
				<el-form-item label="奖励经验">
					<el-input type="number" placeholder="奖励经验" v-model="exp" id="exp" @blur="setExp" style="width: 100upx;">
					</el-input>
				</el-form-item>
				
				<el-form-item label-width="10upx">
					<el-checkbox v-model="bind" @change="setBind">奖励道具是否绑定</el-checkbox>
				</el-form-item>
			</el-form>
		</el-card>
		
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">完成信息</span>
			</view>
			
			<el-form ref="form" label-width="50upx">
				<el-row style="width: 100%">
					<el-col :span="10">
						<el-form-item label="提交任务后行为" label-width="50upx">
							<el-input placeholder="Sn逗号分隔" v-model="submitAct" id="submitAct" @change="setSubmitAct($event)" clearable></el-input>
						</el-form-item>
					</el-col>
					<el-col :span="10" :offset="2">
						<el-form-item label="行为查询">
							<textInput :datas="Actions" placeholder='查询' :method='loadActions' :select="onSelect" :value='noValue'>
							</textInput>
						</el-form-item>
					</el-col>
				</el-row>
				
				<el-form-item label="是否【可交】后自动完成" label-width="70upx">
					<el-checkbox v-model="canAutomaticDeliver" @change="setAutoDeliver"></el-checkbox>
				</el-form-item>
				
				<el-form-item label="任务限时(秒)" label-width="43upx">
					<el-input placeholder="时间限制" v-model="timeLimit" id="timeLimit" @blur="setTimeLimit" style="width: 100upx;" clearable></el-input>
				</el-form-item>
				
				<el-form-item label="显示时间信息" label-width="45upx">
					<el-checkbox v-model="showTimeLimit" @change="setShowTimeLimit" disabled></el-checkbox>
				</el-form-item>
				
				<el-form-item label="完成任务后传送">
					<el-popover
						ref="submitTrans"
						placement="right"
						width="230"
						trigger="focus"
						v-model="isSubmitTransPopoverVisible">
						<el-form :model="transCoord" :rules="transCoordFormRules" ref="transCoordForm" label-width="25upx">
							<el-form-item label="地图ID" prop="mapID">
								<el-input v-model.number="transCoord.mapID" size="mini"></el-input>
							</el-form-item>
							<el-form-item label="X坐标" prop="x">
								<el-input-number v-model="transCoord.x" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item label="Y坐标" prop="y">
								<el-input-number v-model="transCoord.y" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item label="Z坐标" prop="z">
								<el-input-number v-model="transCoord.z" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item label="朝向X" prop="orientX">
								<el-input-number v-model="transCoord.orientX" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item label="朝向Y" prop="orientY">
								<el-input-number v-model="transCoord.orientY" size="mini"></el-input-number>
							</el-form-item>
							<el-form-item>
								<el-button type="primary" @click="onSubmitTransCoord" size="mini">添加</el-button>
								<el-button type="info" @click="resetTransCoord" size="mini">重置</el-button>
							</el-form-item>
						</el-form>
					</el-popover>
					<el-input 
						placeholder="请输入String类型的完成任务后传送坐标(格式:mapID,x,y,z,朝向x,朝向y)" 
						v-model="inputSubmitTrans" 
						id="submitTrans"
						v-popover:submitTrans>
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
	import msg from '../../common/msg.js'
	import util from '../../common/util.js'
	
	export default {
		data() {
			return {
				DropGroup: [],
				Actions: [],
				noValue: '',
				exp: null,
				bind: false,
				questReward: '',
				submitAct: '',
				timeLimit: '',
				showTimeLimit: true,
				canAutomaticDeliver: false,	
				
				/* 传送相关 */
				inputSubmitTrans: '',
				transCoord: {
					mapID: '',
					x: '',
					y: '',
					z: '',
					orientX: '',
					orientY: ''
				},
				transCoordFormRules: {
					mapID: [
						{ required: true, message: '请提供mapID', trigger: 'blur' },
						{ type: 'number', message: '输入的mapID格式错误' }
					],
					x: [
						{ required: true, message: '请提供X坐标', trigger: 'blur' }
					],
					y: [
						{ required: true, message: '请提供Y坐标', trigger: 'blur' }
					],
					z: [
						{ required: true, message: '请提供Z坐标', trigger: 'blur' }
					],
					orientX: [
						{ required: true, message: '请提供朝向X', trigger: 'blur' }
					],
					orientY: [
						{ required: true, message: '请提供朝向Y', trigger: 'blur' }
					]
				},
				
				/* 全局相关 */
				hasSwitchedQuest: false,
				hasSetDefaultValue: false,
				prevTableRowData: null,
				isSubmitTransPopoverVisible: false,
				hasInitializedSubmitTrans: false
			};
		},
		props:['tableRowData'],
		watch: {
			inputSubmitTrans: function () {
				// 如果是切换任务导致的更新，不触发调用该方法 | 如果是首次更新submitTrans值（首次编辑），也不触发调用该方法
				if (!this.hasSwitchedQuest && this.hasInitializedSubmitTrans) {
					// 当输入框中内容变化时，触发调用该方法
					this.onSubmitTransModified()
				} else {
					this.hasInitializedSubmitTrans = true
					this.hasSwitchedQuest = false
				}
			}
		},
		mounted() {
			this.refreshDefaultValues()
		},
		updated() {
			// 如果是用户输入触发的更新，不刷新默认值
			if (this.hasSetDefaultValue) {
				// 如果是由于切换任务导致的更新，刷新默认值
				if (this.prevTableRowData != this.tableRowData) {
					this.refreshDefaultValues()
					this.prevTableRowData = this.tableRowData
					this.hasSwitchedQuest = true
				}
			}
		},
		methods:{
			loadDropGroup: function() {
				if(this.DropGroup.length === 0){
					console.log("loadDropGroup")
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(util.getCurrentUserToken(), "DROPGROUP"),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								this.DropGroup[i] = {
									value: item.sn + ':' + item.name
								};
							}
						},
						fail: () => {
					
						},
						complete: () => {}
					});
				}
			},
			loadActions: function() {
				if(this.Actions.length === 0){
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(util.getCurrentUserToken(), "ACTION"),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								this.Actions[i] = {
									value: item.sn + ':' + item.comment
								};
							}
						}
					});
				}
			},
			setQuestReward: function(item) {
				this.questReward = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'questReward', this.questReward.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setSubmitAct: function(value) {
				console.log(value)
				this.submitAct = value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'submitAct', this.submitAct, this.$store.state.verNum, this)
				}
			},
			setBind: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'bind', this.bind.toString().toUpperCase(), this.$store.state.verNum, this)
				}
			},
			setExp: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'],'exp', this.exp, this.$store.state.verNum, this)
				}
			},
			setTimeLimit: function() {
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST', this.tableRowData['sn'], 'timeLimit', this.timeLimit, this.$store.state.verNum, this)
				}
			},
			setAutoDeliver: function() {
				util.updateDataField('QUEST', this.tableRowData['sn'], 'canAutomaticDeliver',this.canAutomaticDeliver ? "TRUE" : "FALSE", this.$store.state.verNum, this)
			},
			setShowTimeLimit: function() {},
			onSelect: function() {},
			refreshDefaultValues: function() {
				this.questReward = this.tableRowData['questReward']
				this.submitAct = this.tableRowData['submitAct']
				this.bind = this.tableRowData['bind'] != null ? this.tableRowData['bind'].toLowerCase() === 'true' : null
				this.exp = this.tableRowData['exp']
				this.timeLimit = this.tableRowData['timeLimit']
				this.canAutomaticDeliver = this.tableRowData['canAutomaticDeliver'] != null ? this.tableRowData['canAutomaticDeliver'] === 'TRUE' : null
				this.inputSubmitTrans = this.tableRowData['submitTrans']
				
				this.hasSetDefaultValue = true
			},
			// 点击弹窗中的提交按钮后的响应事件
			onSubmitTransCoord: function() {
				this.$refs['transCoordForm'].validate((valid) => {
					if (valid) {
						if (this.inputSubmitTrans != null && this.inputSubmitTrans.length > 0) {
							this.inputSubmitTrans = this.inputSubmitTrans + ';'
						}
						
						if (this.inputSubmitTrans == null) {
							this.inputSubmitTrans = ''
						}
						
						let coords = this.transCoord
						this.inputSubmitTrans = String(this.inputSubmitTrans + coords.mapID + ',' + coords.x + ',' + coords.y + ',' + coords.z + ',' 
						+ coords.orientX + ',' + coords.orientY)
						
						// 隐藏弹窗
						this.isSubmitTransPopoverVisible = false
					} else {
						return false
					}
				})
			},
			// 重置弹窗内容
			resetTransCoord: function() {
				this.$refs['transCoordForm'].resetFields()
			},
			onSubmitTransModified: function() {
				console.log("向submitTrans" + "提交值：" + this.inputSubmitTrans)
				util.updateDataField('QUEST', this.tableRowData['sn'], 'submitTrans', this.inputSubmitTrans, this.$store.state.verNum, this)
			}
		},
		components:{
			textInput
		}
	}
</script>

<style>
	.box-card {
		  width: 300upx;
		  margin-bottom: 10upx;
	}
	
	view {
		line-height: 1.0;
		font-size: 10px;
	}
	
	.clearfix {
		  font-size: 10upx;
		  font-weight: bold
	}
</style>
