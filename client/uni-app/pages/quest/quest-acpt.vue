<template>
	<view style="flex-direction: column;">
		<el-form ref="form" :model="form" label-width="100px">
			<el-form-item label="接受NPC">
				<textInput :datas="NPC" placeholder='接受NPC' :method='loadNpc' :select="setStartNPC" :value='startNpc' id="startNPC">
				</textInput>
			</el-form-item>
			
			<el-form-item label="结束NPC">
				<textInput :datas="NPC" placeholder='结束NPC' :method='loadNpc' :select="setEndNPC" :value='endNpc' id="endNPC">
				</textInput>
			</el-form-item>
			
			<el-form-item label="接受前对话">
				<textInput :datas="Plot" placeholder='接受前对话' :method='loadPlot' :select="setBeforeAcceptPlot" :value='beforeAcceptPlot' id="beforeAcceptPlot">
				</textInput>
			</el-form-item>
			
			<el-form-item label="接受后对话">
				<textInput :datas="Plot" placeholder='接受后对话' :method='loadPlot' :select="setAfterAcceptPlot" :value='afterAcceptPlot' id="afterAcceptPlot">
				</textInput>
			</el-form-item>
			
			<el-form-item label="结束前对话">
				<textInput :datas="Plot" placeholder='结束前对话' :method='loadPlot' :select="setBeforeEndPlot" :value='beforeEndPlot' id="beforeEndPlot">
				</textInput>
			</el-form-item>
			
			<el-form-item label="结束后对话">
				<textInput :datas="Plot" placeholder='结束后对话' :method='loadPlot' :select="setAfterEndPlot" :value='afterEndPlot' id="afterEndPlot">
				</textInput>
			</el-form-item>
		</el-form>
		<view>
			<el-select v-model="career" size="medium">
				<el-option v-for="item in careers" :key="item" :value="item">
				</el-option>
			</el-select>
		</view>

		<button @click="show" v-text="tableRowData">保存</button>
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
				Plot:[],
				startNpc: "",
				endNpc: "",
				beforeAcceptPlot: "",
				afterAcceptPlot: "",
				beforeEndPlot: "",
				afterEndPlot: "",
				tablePlot:"PLOT",
				test:""
			};
		},
		props:['tableRowData'],
		mounted() {
			console.log("Mounted")
			this.refreshDefaultValues()
		},
		updated() {
			console.log("Updated")
			this.refreshDefaultValues()
		},
		methods: {
			loadNpc() {
				if(this.NPC.length === 0){
					console.log("loadNpc")
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(this.$store.state.token, "NPC"),
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
			loadPlot(){
				if(this.Plot.length == 0){
					console.log("loadPlot")
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(this.$store.state.token, "PLOT"),
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
			querySearchNpc(queryString, cb) {
				var Npc = this.NPC;
				var results = queryString ? Npc.filter(this.createStateFilter(queryString)) : Npc;
				clearTimeout(this.timeout);
				this.timeout = setTimeout(() => {
					cb(results);
				});
			},
			querySearchPlot(queryString, cb) {
				var plot = this.Plot;
				var results = queryString ? plot.filter(this.createStateFilter(queryString)) : plot;
				clearTimeout(this.timeout);
				this.timeout = setTimeout(() => {
					cb(results);
				});
			},
			createStateFilter(queryString) {
				return (state) => {
					return (state.value.indexOf(queryString) !== -1);
				};
			},
			handleSelect(item) {
				console.log(item);
			},
			show(){
				console.log(this.startNpc)
				console.log(this.endNpc)
				console.log(this.beforeAcceptPlot)
				console.log(this.afterAcceptPlot)
				console.log(this.beforeEndPlot)
				console.log(this.afterEndPlot)
			},
			select(item){
				this.test = item.value
			},
			setStartNPC(item){
				this.startNpc = item.value
			},
			setEndNPC(item){
				this.endNpc = item.value
			},
			setBeforeAcceptPlot(item){
				this.beforeAcceptPlot = item.value
			},
			setAfterAcceptPlot(item){
				this.afterAcceptPlot = item.value
			},
			setBeforeEndPlot(item){
				this.beforeEndPlot = item.value
			},
			setAfterEndPlot(item){
				this.afterEndPlot = item.value
			},
			refreshDefaultValues(){
				this.startNpc = this.tableRowData['acceptNPC']
				this.endNpc = this.tableRowData['endNPC']
				this.beforeAcceptPlot = this.tableRowData['beforeAcceptPlotId']
				this.afterAcceptPlot = this.tableRowData['afterAcceptPlotId']
				this.beforeEndPlot = this.tableRowData['beforeEndPlotId']
				this.afterEndPlot = this.tableRowData['afterEndPlotId']
			}
		},
		onLoad() {

		},
		components:{
			textInput
		}
	}
</script>

<style>
	view {
		border: #000000 solid 0upx;
		font-size: 15upx;
		margin: 0upx;

		display: flex;
		flex-direction: row;
	}
	.el-form{
		align: middle;
	}
	.el-form-item{
		height: 20upx;
	}
</style>
