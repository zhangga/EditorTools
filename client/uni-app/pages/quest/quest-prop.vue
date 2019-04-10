<template>
	<view>
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务属性</span>
			</view>
			<el-form label-width="30upx">
				<el-form-item label="任务名称">
					<el-input placeholder="请输入String类型名称(对应questName)" v-model="inputQuestName" :prefex="tableRowData['questName']" id="questName">
					</el-input>
				</el-form-item>
				<el-form-item label="任务描述">
					<el-input placeholder="请输入String类型描述(对应questDescription)" type="textarea" :rows="2" v-model="inputQuestDesc" id="questDesc">
					</el-input>
				</el-form-item>
				<el-form-item label="任务类型">
					<el-select v-model="selectedQuestType" size="medium" id="questType">
						<el-option v-for="questType in questTypes" :key="questType" :value="questType"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="等级限制">
					<el-input-number v-model="inputQuestLevel" :min="0"></el-input-number>
				</el-form-item>
			</el-form>
		</el-card>
		
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务显示</span>
			</view>
			<el-form label-width="0upx">
				<el-form-item>
					<el-checkbox v-model="selectedHidden">任务是否隐藏</el-checkbox>
				</el-form-item>
				<el-form-item>
					<el-checkbox v-model="selectedCanAutomaticDeliver">是否【可交】自动完成</el-checkbox>
				</el-form-item>
				<el-form-item>
					<el-checkbox v-model="selectedCanAutomaticAccept">是否【可接】自动接取</el-checkbox>
				</el-form-item>
				<el-form-item>
					<span class="text">【说明：方框为勾选，默认不勾】</span>
				</el-form-item>
			</el-form>
		</el-card>
		
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务时间段</span>
			</view>
			<el-form label-width="30upx">
				<el-form-item label="发放时间段">
					
				</el-form-item>
				<el-form-item label="发放间隔">
					
				</el-form-item>
				<el-form-item>
					<span class="text">【说明：发放时间段和发放时间间隔可以同时生效，也可单独生效。下拉内容含有每隔一天、每隔一星期、每隔一月、每隔一段时间。每隔一段时间需要策划填写后面的具体秒数。】</span>
				</el-form-item>
			</el-form>
		</el-card>
	</view>
</template>

<script>
	import config from '../../common/config.js'
	
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
				
				/* 任务时间段相关 */
				
				
				/* 全局相关 */
				hasSetDefaultValue: false,
				prevTableRowData: null
			};
		},
		props: ['tableRowData', 'questTypes'],
		mounted: function() {
			console.log("Mounted")
			this.refreshDefaultValues()
		},
		updated: function() {
			console.log("Updated")
			console.log(this.tableRowData)
			
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
				document.getElementById("questName").defaultValue = this.tableRowData['questName']
				document.getElementById("questDesc").defaultValue = this.tableRowData['questDescription']
				
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
		}
	}
</script>

<style>
  .box-card {
	  width: 300upx;
	  margin-bottom: 10upx;
  }
  
  .el-input {
	  font-size: 6upx;
	  font-family: "PingFang SC"
  }
  
  .text {
	  font-size: 6upx;
	  font-family: "PingFang SC"
  }
</style>
