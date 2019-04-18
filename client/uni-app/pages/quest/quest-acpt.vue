<template>
	<view style="flex-direction: column;">
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务接受与结束</span>
			</view>
			<el-form label-width="100px">
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

				<el-form-item label-width="10upx">
					<el-checkbox v-model="showAcceptedEffect" @change="setShowAcceptedEffect">是否显示接受特效</el-checkbox>
				</el-form-item>

				<el-form-item label-width="10upx">
					<el-checkbox v-model="showFinishedEffect" @change="setShowFinishedEffect">是否显示完成任务特效</el-checkbox>
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
				careers: [
					"单个",
					"与关系",
					"或关系"
				],
				career: "单个",
				NPC: [],
				Plot: [],
				startNpc: "0",
				endNpc: "0",
				beforeAcceptPlot: "0",
				afterAcceptPlot: "0",
				beforeEndPlot: "0",
				afterEndPlot: "0",
				tablePlot: "PLOT",
				showAcceptedEffect: true,
				showFinishedEffect: true,

				/* 全局相关 */
				hasSetDefaultValue: false,
				prevTableRowData: null
			};
		},
		props: ['tableRowData'],
		mounted() {
			this.refreshDefaultValues()
		},
		updated() {
			//console.log("Updated")
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
			loadNpc() {
				if (this.NPC.length === 0) {
					console.log("loadNpc")
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(util.getCurrentUserToken(), "NPC"),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								this.NPC[i] = {
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
			loadPlot() {
				if (this.Plot.length === 0) {
					console.log("loadPlot")
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(util.getCurrentUserToken(), "PLOT"),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								this.Plot[i] = {
									value: item.sn + ':' + item["1"]
								};
							}
						},
						fail: () => {

						},
						complete: () => {}
					});
				}
			},
			show() {
				console.log(this.startNpc)
				console.log(this.endNpc)
				console.log(this.beforeAcceptPlot)
				console.log(this.afterAcceptPlot)
				console.log(this.beforeEndPlot)
				console.log(this.afterEndPlot)
				console.log(this.showAcceptedEffect)
				console.log(this.showFinishedEffect)
			},
			setStartNPC(item) {
				this.startNpc = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'acceptNPC',this.startNpc.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setEndNPC(item) {
				this.endNpc = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'endNPC',this.endNpc.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setBeforeAcceptPlot(item) {
				this.beforeAcceptPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'beforeAcceptPlotId',this.beforeAcceptPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setAfterAcceptPlot(item) {
				this.afterAcceptPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'afterAcceptPlotId',this.afterAcceptPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setBeforeEndPlot(item) {
				this.beforeEndPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'beforeEndPlotId',this.beforeEndPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setAfterEndPlot(item) {
				this.afterEndPlot = item.value
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'afterEndPlotId',this.afterEndPlot.split(':')[0], this.$store.state.verNum, this)
				}
			},
			setShowAcceptedEffect(){
				var val = '0'
				if(this.showAcceptedEffect){
					val = '1'
				}
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'showAcceptedEffect',val, this.$store.state.verNum, this)
				}
			},
			setShowFinishedEffect(){
				var val = '0'
				if(this.showFinishedEffect){
					val = '1'
				}
				if (this.tableRowData['sn'] != null) {
					util.updateDataField('QUEST',this.tableRowData['sn'],'showFinishedEffect',val, this.$store.state.verNum, this)
				}
			},
			refreshDefaultValues() {
				this.startNpc = this.tableRowData['acceptNPC'] != null ? this.findNPC(this.tableRowData['acceptNPC']) : this.startNpc
				this.endNpc = this.tableRowData['endNPC'] != null ? this.findNPC(this.tableRowData['endNPC']) : this.endNpc
				this.beforeAcceptPlot = this.tableRowData['beforeAcceptPlotId'] != null ? this.findPlot(this.tableRowData['beforeAcceptPlotId']) : this.beforeAcceptPlot
				this.afterAcceptPlot = this.tableRowData['afterAcceptPlotId'] != null ? this.findPlot(this.tableRowData['afterAcceptPlotId']) : this.afterAcceptPlot
				this.beforeEndPlot = this.tableRowData['beforeEndPlotId'] != null ? this.findPlot(this.tableRowData['beforeEndPlotId']) : this.beforeEndPlot
				this.afterEndPlot = this.tableRowData['afterEndPlotId'] != null ? this.findPlot(this.tableRowData['afterEndPlotId']) : this.afterEndPlot
				
				if (this.tableRowData['showAcceptedEffect'] != null) {
					this.showAcceptedEffect = this.tableRowData['showAcceptedEffect'] !== '0'
				}
				
				if (this.tableRowData['showFinishedEffect'] != null) {
					this.showFinishedEffect = this.tableRowData['showFinishedEffect'] !== '0'
				}
				
			},
			findNPC(NpcID) {
				if (NpcID == '') {
					return NpcID
				}
				for (let i = 0; i < this.NPC.length; i++) {
					if (this.NPC[i].value.indexOf(NpcID) === 0) {
						return this.NPC[i].value
					}
				}
				return NpcID
			},
			findPlot(plotID) {
				if (plotID == '') {
					return plotID
				}
				for (let i = 0; i < this.Plot.length; i++) {
					if (this.Plot[i].value.indexOf(plotID) === 0) {
						return this.Plot[i].value
					}
				}
				return plotID
			}
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
</style>
