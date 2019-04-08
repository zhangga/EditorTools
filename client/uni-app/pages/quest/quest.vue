<template>
	<view>
		<van-row>
			<van-col class="quest-select">
				<van-tree-select
					:items="items"
					:main-active-index="mainActiveIndex"
					:active-id="activeId"
					:height=1000
					@navclick="onNavClick"
					@itemclick="onItemClick"
				/>
			</van-col>
			<van-col class="quest-details">
				<van-tabs v-model="activeTab">
					<van-tab v-for="index in tabConfig.length" :title="tabConfig[index-1]">
						<view v-if="index == 1">
							<quest-prop/>
						</view>
						<view v-if="index == 2">
							<quest-acpt/>
						</view>
						<view v-if="index == 3">
							<quest-goal/>
						</view>
						<view v-if="index == 4">
							<quest-comp/>
						</view>
					</van-tab>
				</van-tabs>
			</van-col>
		</van-row>
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
		onLoad() {
			// 任务页签的配置
			this.tabConfig = config.ExcelConfig()['QuestTab'];
			this.onLoadQuestBrief();
		},
		methods: {
			onNavClick(index) {
				this.mainActiveIndex = index;
			},
			onItemClick(data) {
				this.activeId = data.id;
			},
			onLoadQuestBrief() {
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_all_quest_brief(this.$store.state.token),
					success: res => {
						this.items = res.data['data']
						// 排序
						for (let i = 0; i < this.items.length; i++) {
							let children = this.items[i]['children']
							children.sort(function(a, b) {
								return a['id'] - b['id']
							})
						}
					}
				});
			}
		}
	}
</script>

<style>
	.quest-select {
		width: 30%;
	}
	
	.quest-details {
		width: 70%;
	}
</style>
