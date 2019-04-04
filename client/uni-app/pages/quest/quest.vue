<template>
	<div class="quest-select">
		<van-tree-select
			:items="items"
			:main-active-index="mainActiveIndex"
			:active-id="activeId"
			:height=1000
			@navclick="onNavClick"
			@itemclick="onItemClick"
		/>
	</div>
</template>

<script>
	import msg from '../../common/msg.js'
	export default {
		data() {
			return {
				items: [],
				mainActiveIndex: 0,
				activeId: 1
			};
		},
		computed: {
		},
		onLoad() {
			uni.request({
				url: msg.url(),
				method: 'GET',
				data: msg.get_all_quest_brief(this.$store.state.token),
				success: res => {
					this.items = res.data['data']
				}
			});
		},
		methods: {
			onNavClick(index) {
				this.mainActiveIndex = index;
			},
			onItemClick(data) {
				this.activeId = data.id;
			}
		}
	}
</script>

<style>
	.quest-select {
		width: 30%;
	}
</style>
