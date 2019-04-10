<template>
	<view>
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务属性</span>
			</view>
			<el-form ref="form" :model="form" label-width="100px">
				<el-form-item label="任务名称">
					<el-input placeholder="请输入String类型名称(对应questName)" v-model="inputQuestName" :prefex="tableRowData['questName']" id="questName">
					</el-input>
				</el-form-item>
				<el-form-item label="任务描述">
					<el-input placeholder="请输入String类型描述(对应questDescription)" type="textarea" :rows="2" v-model="inputQuestDesc" id="questDesc">
					</el-input>
				</el-form-item>
				<el-form-item label="任务类型">
					<el-select v-model="selectedQuestType" size="medium">
						<el-option v-for="questType in questTypes" :key="questType" :value="questType"></el-option>
					</el-select>
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
				inputQuestName: '',
				inputQuestDesc: '',
				selectedQuestType: '',
			};
		},
		props: ['tableRowData', 'questTypes'],
		mounted: function() {
			console.log("Mounted")
			this.refreshDefaultValues()
		},
		updated: function() {
			console.log("Updated")
			this.refreshDefaultValues()
		},
		methods: {
			refreshDefaultValues: function() {
				document.getElementById("questName").defaultValue = this.tableRowData['questName']
				document.getElementById("questDesc").defaultValue = this.tableRowData['questDescription']
			}
		}
	}
</script>

<style>
  /* .text {
      font-size: 14px;
  }
  
  .header {
      font-size: 50px;
  }

  .item {
      margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
	  display: table;
      content: "";
  }
  .clearfix:after {
	  clear: both
  }
 */
  .box-card {
	  width: 480px;
  }
  
  el-input {
	  font-size: 9upx;
	  font-family: "PingFang SC"
  }
</style>
