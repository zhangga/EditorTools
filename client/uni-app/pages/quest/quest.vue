<template>
	<view>
		<el-container style="height: 800upx" v-if="dataLoadComplete" class="nav-bar">
			<el-aside width="30%" style="background-color: rgb(238, 241, 246)">
				<el-header height="80px">
					<el-row>
						<el-col :span="8">
							<span>任务菜单</span>
						</el-col>
						<el-col :span="15">
							<el-cascader placeholder="按照名称/ID搜索" filterable style="position: absolute; right: 10upx"></el-cascader>
						</el-col>
					</el-row>
				</el-header>
				<el-menu
					active-text-color="#ffd04b" 
					ref="menu"
					@open="handleOpen">
					<el-submenu v-for="index in items.length" :index='String(index)' >
						<template slot="title">
							<i class="el-icon-menu"></i>
							<span>{{items[index - 1]['text']}}</span>
							<span class="dot">{{items[index - 1].children.length > 999 ? "999+" : items[index - 1].children.length}}</span>
						</template>
						<el-menu-item-group v-for="subIndex in items[index - 1].children.length" :index="String(index)">
							<el-menu-item :index="String(index) + '.'+ String(subIndex) + '.' + items[index - 1].children[subIndex-1]['id']"
							 @click="onItemClick">
								{{items[index - 1].children[subIndex - 1]['text']}}
							</el-menu-item>
						</el-menu-item-group>
					</el-submenu>
				</el-menu>
			</el-aside>
			
			<el-container>
				<el-main>
				<el-tabs v-model="activeTab" class="quest-details">
					<el-tab-pane :label='tabConfig[0]' :name='tabConfig[0]'>
						<quest-prop v-bind:tableRowData="currSelectedQuestData" v-bind:questTypes="questTypes" v-if="hasSelectedRowData"/>
					</el-tab-pane>
					<el-tab-pane :label='tabConfig[1]' :name='tabConfig[1]'><quest-acpt v-bind:tableRowData="currSelectedQuestData"/></el-tab-pane>
					<el-tab-pane :label='tabConfig[2]' :name='tabConfig[2]'><quest-goal v-bind:tableRowData="currSelectedQuestData"/></el-tab-pane>
					<el-tab-pane :label='tabConfig[3]' :name='tabConfig[3]'><quest-comp v-bind:tableRowData="currSelectedQuestData"/></el-tab-pane>
				</el-tabs>
				</el-main>
			</el-container>
		</el-container>

	</view>
</template>

<script>
	import msg from '../../common/msg.js'
	import config from '../../common/config.js'
	
	import questProp from './quest-prop.vue'
	import questAcpt from './quest-acpt.vue'
	import questGoal from './quest-goal.vue'
	import questComp from './quest-comp.vue'
	
	export default {
		data() {
			return {
				tabConfig: [],
				items: [],
				mainActiveIndex: 0,
				activeSn: 0,
				activeTab: 0,
				dataLoadComplete: false,
				hasSelectedRowData: false,
				questTypes: [],
				currSelectedQuestData: null
			};
		},
		computed: {
		},
		components: {
			questProp,
			questAcpt,
			questGoal,
			questComp
		},
		onLoad: function() {
			// 任务页签的配置
			this.tabConfig = config.ExcelConfig()['QuestTab'];
			this.onLoadQuestBrief();
		},
		methods: {
			handleOpen: function(key, keyPath) {
				console.log(key);
			},
			onNavClick: function(index) {
				this.mainActiveIndex = index;
			},
			onItemClick: function(e) {
				// 解析点击项ID
				var splittedIndex = e.index.split('.')
				
				console.log("当前顶级菜单" + splittedIndex[0])
				
				// 关闭之前展开的顶级菜单（保留当前激活项目对应的顶级菜单）
				for (var i = 1; i <= this.items.length; i++) {
					if (i != splittedIndex[0]) {
						console.log("关闭顶级菜单" + i)
						this.$refs.menu.close(String(i))
					}
					else {
						this.$refs.menu.open(String(i))
					}
				}
				
				this.activeSn = splittedIndex[2]
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_table_data_by_sn(this.$store.state.token, 'QUEST', this.activeSn),
					success: res => {
						this.currSelectedQuestData = JSON.parse(res.data['data'])
						this.hasSelectedRowData = true
					},
					fail: res => {
						uni.showToast({
							title: '获取不到对应的任务数据！',
							icon: 'none',
							duration: 2000
						});
					}
				});
			},
			onLoadQuestBrief: function() {
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_all_quest_brief(this.$store.state.token),
					success: res => {
						this.items = res.data['data'];
						// 排序
						for (let i = 0; i < this.items.length; i++) {
							this.questTypes.push(this.items[i]['text'])
							let children = this.items[i]['children']
							children.sort(function(a, b) {
								return a['id'] - b['id'];
							});
						}
						
						this.activeTab = this.tabConfig[0];
						this.dataLoadComplete = true;
					}
				});
			},
		}
	}
</script>

<style>
	.nav-bar {
		height: 100%;
		border: 1px solid #eee
	}
	.quest-details {
		margin-left: 3%;
		width: 94%;
		margin-left: 3%;
	}
	
	.dot {
		background:#409EFF; 
		width: 22px;
		height: 22px;
		border-radius: 50%; 
		
		font-size: 8px; 
		color: #FFFFFF;
		text-align: center;
		line-height: 22px;
		
		position: absolute;
		top: 30%;
		right: 20%;
	}
	
	.el-header {
		background-color: #909399;
		color: #333;
		/* line-height: 60px; */
		font-size: 25px;
	}
	
	.el-row {
		padding-top: 10upx;
		padding-bottom: 10upx;
	}

</style>
