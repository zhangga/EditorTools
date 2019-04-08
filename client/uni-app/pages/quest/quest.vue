<template>
	<view>
		<van-row>
			<van-col span="10">
				<van-tree-select
					:items="items"
					:main-active-index="mainActiveIndex"
					:active-id="activeId"
					:height=1000
					@navclick="onNavClick"
					@itemclick="onItemClick"
				/>
			</van-col>
			<van-col span="10">
				<van-tabs v-model="activeTab">
					<van-tab v-for="index in 4" :title="'tab' + index">
						content of tab {{ index }}
					</van-tab>
				</van-tabs>
			</van-col>
		</van-row>
	</view>
</template>

<script>
	import msg from '../../common/msg.js'
	
	export default {
		data() {
			return {
				items: [],
				mainActiveIndex: 0,
				activeId: 1,
				activeTab: 0,
			};
		},
		computed: {
		},
		onLoad() {
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
	
</style>
