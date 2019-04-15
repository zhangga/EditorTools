<template>
	<view>
		<!-- 任务属性 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务属性</span>
			</view>
			<el-form label-width="30upx">
				<el-form-item label="任务名称">
					<el-input 
					placeholder="请输入String类型名称(对应questName)" 
					v-model="inputQuestName" 
					id="questName" 
					@change="onFormItemModified($event)" 
					@click.native="updateCurrentFocusedItem">
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
						@click.native="updateCurrentFocusedItem">
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
				<el-form-item label="等级限制" id="questLevel">
					<el-input-number 
						v-model="inputQuestLevel" 
						:min="0" 
						@change="onUpdateQuestLevel">
					</el-input-number>
				</el-form-item>
			</el-form>
		</el-card>
		
		<!-- 任务显示 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务显示</span>
			</view>
			<el-form label-width="0upx">
				<el-form-item>
					<el-checkbox 
						v-model="selectedHidden" 
						id="hidden"
						@change="onFormItemModified" 
						@click.native="updateCurrentFocusedItem">
						任务是否隐藏
					</el-checkbox>
				</el-form-item>
				<el-form-item>
					<el-checkbox 
						v-model="selectedCanAutomaticDeliver"
						id="canAutomaticDeliver"
						@change="onFormItemModified" 
						@click.native="updateCurrentFocusedItem">
						是否【可交】自动完成
					</el-checkbox>
				</el-form-item>
				<el-form-item>
					<el-checkbox 
						v-model="selectedCanAutomaticAccept" 
						id="canAutomaticAccept"
						@change="onFormItemModified" 
						@click.native="updateCurrentFocusedItem">
						是否【可接】自动接取
					</el-checkbox>
				</el-form-item>
				<el-form-item>
					<span class="text">【说明：方框为勾选，默认不勾】</span>
				</el-form-item>
			</el-form>
		</el-card>
		
		<!-- 任务时间段 -->
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务时间段</span>
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
		
		<!-- 用于触发组件之间的数据同步与更新 -->
		<span style="display:none"> {{tableRowData['questName']}} </span>
	</view>
</template>

<script>
	import config from '../../common/config.js'
	import util from '../../common/util.js'
	
	export default {
		data() {
			return {
				/* 任务属性卡片相关 */
				inputQuestName: '',
				inputQuestDesc: '',
				selectedQuestType: '',
				inputQuestLevel: '',
				
				/* 任务显示卡片相关 */
				selectedHidden: false,
				selectedCanAutomaticDeliver: false,
				selectedCanAutomaticAccept: false,
				
				/* 任务时间段相关 (暂无) */
				selectedQuestAvailTime: [],
				questAvailIntervalOptions: [
					{"id": 0, "desc":"每隔一天"}, 
					{"id": 1, "desc": "每隔一星期"}, 
					{"id":2, "desc": "每隔一个月"}, 
					{"id": 3, "desc": "每隔一段时间"}],
				selectedQuestAvailInterval: '',
				inputQuestAvailIntervalSeconds: '',
				isIntervalOfSecondSelected: false,
				
				/* 全局相关 */
				hasSetDefaultValue: false,
				prevTableRowData: null,
				currentClickedFormItem: ''
			};
		},
		props: ['tableRowData', 'questTypes', 'currTableName'],
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
				this.inputQuestName = this.tableRowData['questName']
				
				if (this.tableRowData['questDescription'] != null) {
					this.inputQuestDesc = this.tableRowData['questDescription']
				}
				
				if (this.tableRowData['questType'] != null) {
					this.selectedQuestType = this.questTypes[parseInt(this.tableRowData['questType'], 10) - 1]
				}

				if (this.tableRowData['questLevel'] != null) {
					this.inputQuestLevel = this.tableRowData['questLevel']
				}
				
				this.selectedHidden = this.tableRowData['hidden'] == null ? false : (this.tableRowData['hidden'] == "TRUE")
				this.selectedCanAutomaticAccept = this.tableRowData['canAutomaticAccept'] == null ? false : (this.tableRowData['canAutomaticAccept'] == "TRUE")
				this.selectedCanAutomaticDeliver = this.tableRowData['canAutomaticDeliver'] == null ? false : (this.tableRowData['canAutomaticDeliver'] == "TRUE")
			},
			onQuestAvaiIntervalChanged: function(value) {
				this.selectedQuestAvailInterval = value
			},
			onFormItemModified: function(updatedValue) {
				var currItem = this.currentClickedFormItem
				
				if (currItem == 'questName' || currItem == 'questDescription') {
					console.log("向" + currItem + "提交值：" + updatedValue)
					util.updateDataField(this.currTableName, this.tableRowData['sn'], currItem, updatedValue, this.$store.state.verNum, this)
				} else if (currItem == 'questType') {
					console.log("向" + currItem + "提交值：" + parseInt(this.questTypes.indexOf(updatedValue) + 1))
					util.updateDataField(this.currTableName, this.tableRowData['sn'], currItem, parseInt(this.questTypes.indexOf(updatedValue) + 1), this.$store.state.verNum, this)
				} else if (currItem == 'hidden' || currItem == 'canAutomaticDeliver' || currItem == 'canAutomaticAccept') {
					console.log("向" + currItem + "提交值：" + (updatedValue ? "TRUE" : "FALSE"))
					util.updateDataField(this.currTableName, this.tableRowData['sn'], currItem, (updatedValue ? "TRUE" : "FALSE"), this.$store.state.verNum, this)
				}
			},
			// 获取当前点击表单组件的id
			updateCurrentFocusedItem: function(event) {
				var paths = event.path
				for (var i = 0; i < paths.length; i++) {
					if (paths[i].id != '') {
						this.currentClickedFormItem = paths[i].id
						break
					}
				}
				console.log("点击了表单组件：" + this.currentClickedFormItem)
			},
			
			// el-input-number类型的组件较为特殊，原生的click方法会在@change方法之后被调用，导致方法的执行顺序出错
			onUpdateQuestLevel: function(updatedValue) {
				console.log("向" + "questLevel" + "提交值：" + updatedValue)
				util.updateDataField(this.currTableName, this.tableRowData['sn'], "questLevel", updatedValue, this.$store.state.verNum, this)
			}
		}
	}
</script>

<style>
  .box-card {
	  width: 300upx;
	  margin-bottom: 10upx;
  }
  
  .el-input {
	  /* font-size: 6upx; */
	  font-family: "PingFang SC"
  }
  
  .text {
	  font-size: 6upx;
	  font-family: "PingFang SC"
  }
  
  .clearfix {
	  font-size: 10upx;
	  font-weight: bold
  }
</style>
