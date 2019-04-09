<template>
	<view>
		<el-container v-if="dataLoadComplete" class="nav-bar">
			<el-aside width="30%" style="background-color: rgb(238, 241, 246)">
				<el-menu 
					:default-openeds="['1']"
					active-text-color="#ffd04b" 
					v-for="index in items.length"
					@open="handleOpen">
					<el-submenu :index='String(index)'>
						<template slot="title">
							<i class="el-icon-menu"></i>
							<el-badge :value="items[index - 1].children.length" type="primary">
								<span>{{items[index - 1]['text']}}</span>
							</el-badge>
						</template>
						<el-menu-item-group v-for="subIndex in items[index - 1].children.length">
							<el-menu-item :index="String(index) + '.'+ String(subIndex)">{{items[index - 1].children[subIndex - 1]['text']}}</el-menu-item>
						</el-menu-item-group>
					</el-submenu>
				</el-menu>
			</el-aside>
			
			<el-container>
				<el-tabs v-model="activeTab" class="quest-details">
					<el-tab-pane :label='tabConfig[0]' :name='tabConfig[0]'><quest-prop/></el-tab-pane>
					<el-tab-pane :label='tabConfig[1]' :name='tabConfig[1]'><quest-acpt/></el-tab-pane>
					<el-tab-pane :label='tabConfig[2]' :name='tabConfig[2]'><quest-goal/></el-tab-pane>
					<el-tab-pane :label='tabConfig[3]' :name='tabConfig[3]'><quest-comp/></el-tab-pane>
				</el-tabs>
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
				activeId: 1,
				activeTab: 0,
				dataLoadComplete: false
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
			onItemClick: function(data) {
				this.activeId = data.id;
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
							let children = this.items[i]['children']
							children.sort(function(a, b) {
								return a['id'] - b['id'];
							});
						}
						
						this.activeTab = this.tabConfig[0];
						this.dataLoadComplete = true;
					}
				});
			}
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

</style>
