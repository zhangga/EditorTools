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
			
			<el-form ref="form" label-width="40upx">
				<el-form-item label="任务限时(秒)">
					<el-input placeholder="时间限制" v-model="timeLimit" id="timeLimit" @blur="setTimeLimit" style="width: 100upx;" clearable></el-input>
				</el-form-item>
				
				<el-form-item label="显示时间信息">
					<el-checkbox v-model="showTimeLimit" @change="setShowTimeLimit" disabled></el-checkbox>
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
				DropGroup:[],
				exp:0,
				bind:false,
				questReward:'',
				timeLimit:0,
				showTimeLimit:true,
				
				/* 全局相关 */
				hasSetDefaultValue: false,
				prevTableRowData: null
			};
		},
		props:['tableRowData'],
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
				}
			} else {
				// 如果还未设置过默认值，执行设置
				this.refreshDefaultValues()
				this.hasSetDefaultValue = true;
				this.prevTableRowData = this.tableRowData;
			}
		},
		methods:{
			loadDropGroup(){
				if(this.DropGroup.length === 0){
					console.log("loadDropGroup")
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(this.$store.state.token, "DROPGROUP"),
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
			setQuestReward(item){
				this.questReward = item.value
				util.updateDataField('QUEST',this.tableRowData['sn'],'questReward',this.questReward.split(':')[0])
			},
			setBind(){
				util.updateDataField('QUEST',this.tableRowData['sn'],'bind',this.bind.toString().toUpperCase())
			},
			setExp(){
				util.updateDataField('QUEST',this.tableRowData['sn'],'exp',this.exp)
			},
			setTimeLimit(){
				util.updateDataField('QUEST',this.tableRowData['sn'],'timeLimit',this.timeLimit)
			},
			setShowTimeLimit(){
			},
			refreshDefaultValues(){
				this.questReward = this.tableRowData['questReward']
				this.bind = this.tableRowData['bind'].toLowerCase() === 'true'
				this.exp = this.tableRowData['exp']
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
		//border: #000000 solid 1upx;
		font-size: 15upx;
		margin: 2upx;
	
		display: flex;
		flex-direction: row;
	}
</style>
