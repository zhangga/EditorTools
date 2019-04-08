<template>
		<view>
			<view style="flex:3;">
				<el-tree :data="treeData":props="defaultProps" @node-click="handleNodeClick">
				</el-tree>
			</view>
			<view style="flex:7;">
				<view style="flex-direction: column;flex: 1;">
					<el-tabs activeName="property">
						<el-tab-pane label="任务属性" name="property">
							<form @submit="propertySubmit">
								<view>
									<view style="flex: 1;">
										<el-input name="taskID" placeholder="任务ID" v-model="taskID" />
									</view>
													
									<view style="flex: 1;">
										<el-input name="taskName" placeholder="任务名称" v-model="taskName" />
									</view>
								</view>
								<view>
									<view style="flex: 1;">
										<el-select class="item-choose" v-model="value" size="medium">
											<el-option v-for="item in options" :key="item" :value="item">
											</el-option>
										</el-select>
									</view>			
									<view style="flex: 1;">
										<el-checkbox name="giveUp" v-model="checked">
											可以手动放弃
										</el-checkbox>
									</view>
								</view>
					
								<view>
									<view style="flex: 1;">
										<el-input name="seqID" placeholder="排序id" v-model="seqID" />
									</view>
													
									<view style="flex: 1;">
										<el-checkbox name="isHidden" v-model="isHidden">
											隐藏任务
										</el-checkbox>
									</view>
								</view>
								<view>
									<button formType="submit">保存</button>
								</view>
							</form>
						</el-tab-pane>
						<el-tab-pane label="任务接取" name="accept">
							<form @submit="acceptSubmit">
								<view>
									<el-input name="levelMin" placeholder="等级下限" v-model="levelMin" />
								</view>
					
								<view>
									<el-input name="levelMax" placeholder="等级上限" v-model="levelMax" />
								</view>
					
								<view>
									<el-input name="preTaskID" placeholder="前置任务id" v-model="preTaskID" />
								</view>
					
								<view>
									<el-select v-model="career" size="medium">
										<el-option v-for="item in careers" :key="item" :value="item">
										</el-option>
									</el-select>
								</view>
					
								<view>
									<el-select v-model="race" size="medium">
										<el-option v-for="item in races" :key="item" :value="item">
										</el-option>
									</el-select>
								</view>
					
								<view>
									<el-input name="otherConditionID" placeholder="其他条件id" v-model="otherConditionID" />
								</view>
					
								<view>
									<el-input name="startNPC" placeholder="接受NPC" v-model="startNPC" />
								</view>
					
								<view>
									<el-input name="endNPC" placeholder="结束NPC" v-model="endNPC" />
								</view>
					
								<view>
									<el-input name="startPlotID" placeholder="接任务对话id" v-model="startPlotID" />
								</view>
					
								<view>
									<el-input name="endPlotID" placeholder="交任务对话id" v-model="endPlotID" />
								</view>
					
								<view>
									<el-input name="startNotEndPlotID" placeholder="已接未达目标对话id" v-model="startNotEndPlotID" />
								</view>
					
								<view class="uni-btn-v">
									<button formType="submit">保存</button>
								</view>
							</form>
						</el-tab-pane>
						<el-tab-pane label="任务目标" name="target">
							111
						</el-tab-pane>
						<el-tab-pane label="任务完成" name="complete">
							222
						</el-tab-pane>
					</el-tabs>
				</view>
			</view>
		</view>
</template>

<script>
	import store from "../../store/index.js"
	export default {
		data() {
			return {
				treeData:[{
					label:"主线任务",
					children:[{
						label:"item1.1",
						},
						{
							label:"item1.2",
						},
						{
							label:"item1.3",
						}]
				},
				{
					label:"结局任务",
					children:[{
						label:"item1.1",
						},
						{
							label:"item1.2",
						},
						{
							label:"item1.3",
						}]	
				},
				{
					label:"关系任务",
					children:[{
						label:"item1.1",
						},
						{
							label:"item1.2",
						},
						{
							label:"item1.3",
						}]
				},
				{
					label:"奇遇任务",
					children:[{
						label:"item1.1",
						},
						{
							label:"item1.2",
						},
						{
							label:"item1.3",
						}]
				}],
				defaultProps: {
					children: 'children',
					label: 'label'
				},
				options: [
					"主线任务",
					"结局任务",
					"关系任务",
					"奇遇任务"
				],
				value: "主线任务",
				taskID: "",
				taskName: "",
				checked: false,
				seqID: "",
				isHidden: false,

				levelMin: "",
				levelMax: "",
				preTaskID: "",
				careers: [
					"职业1",
					"职业2",
					"职业3"
				],
				career: "职业1",
				races: [
					"种族1",
					"种族2",
					"种族3"
				],
				race: "种族1",
				otherConditionID: "",
				startNPC: "",
				endNPC: "",
				startPlotID: "",
				endPlotID: "",
				startNotEndPlotID: ""
			}
		},
		methods: {
			propertySubmit(){
				console.log(this.taskID)
				console.log(this.taskName)
				console.log(this.value)
				console.log(this.checked)
				console.log(this.seqID)
				console.log(this.isHidden)
			},
			acceptSubmit(){
				console.log(this.levelMin)
				console.log(this.levelMax)
				console.log(this.preTaskID)
				console.log(this.career)
				console.log(this.race)
				console.log(this.otherConditionID)
				console.log(this.startNPC)
				console.log(this.endNPC)
				console.log(this.startPlotID)
				console.log(this.endPlotID)
				console.log(this.startNotEndPlotID)
			},
			handleNodeClick(data){
				console.log(data.label);
			}
		}
	}
</script>
<style>
	view{
		border: #000000 solid 1upx;
		font-size: 15upx;
		margin: 1upx;
		
		display: flex;
		flex-direction: row;
	}
	.el-input{
		border: #111111;
	}
</style>
