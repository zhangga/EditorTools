<template>
	<view style="flex-direction: column;">
		<view>
			<el-select v-model="career" size="medium">
				<el-option v-for="item in careers" :key="item" :value="item">
				</el-option>
			</el-select>
		</view>

		<view>
			<el-autocomplete v-model="startNpc" :fetch-suggestions="querySearchNpc" placeholder="接受NPC" @select="handleSelect">
			</el-autocomplete>
		</view>

		<view>
			<el-autocomplete v-model="endNpc" :fetch-suggestions="querySearchNpc" placeholder="结束NPC" @select="handleSelect">
			</el-autocomplete>
		</view>

		<view>
			<el-autocomplete v-model="beforeAcceptPlot" :fetch-suggestions="querySearchPlot" placeholder="接受前对话" @select="handleSelect">
			</el-autocomplete>
		</view>

		<view>
			<el-autocomplete v-model="afterAcceptPlot" :fetch-suggestions="querySearchPlot" placeholder="接受后对话" @select="handleSelect">
			</el-autocomplete>
		</view>

		<view>
			<el-autocomplete v-model="beforeEndPlot" :fetch-suggestions="querySearchPlot" placeholder="结束前对话" @select="handleSelect">
			</el-autocomplete>
		</view>

		<view>
			<el-autocomplete v-model="afterEndPlot" :fetch-suggestions="querySearchPlot" placeholder="结束后对话" @select="handleSelect">
			</el-autocomplete>
		</view>
		
		<textInput :datas="NPC" placeholder='测试' :method='loadNpc' :select="select" id="test123"></textInput>
		<button @click="show">保存</button>
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
				console.log(this.tableRowData)
			},
			select(item){
				this.test = item.value
			},
			refreshDefaultValues(){
				document.getElementById("test123").defaultValue = this.tableRowData['questName']
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
		border: #000000 solid 1upx;
		font-size: 15upx;
		margin: 1upx;

		display: flex;
		flex-direction: row;
	}
</style>
