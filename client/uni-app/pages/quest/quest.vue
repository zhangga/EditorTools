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
				questBrief: [],
				items: [
					{
    // name of the parent node
    text: 'All Cities',
    // leaves of this parent node
    children: [
      {
        // name of the leaf node
        text: 'Washington',
        // id of the leaf node, component highlights leaf node by comparing the activeId with this.
        id: 1,
        // disable options
        disabled: true
      },
      {
        text: 'Baltimore',
        id: 2
      }
    ]
  },
  {
    // name of the parent node
    text: 'All Cities',
    // leaves of this parent node
    children: [
      {
        // name of the leaf node
        text: 'Washington',
        // id of the leaf node, component highlights leaf node by comparing the activeId with this.
        id: 1,
        // disable options
        disabled: true
      },
      {
        text: 'Baltimore',
        id: 2
      }
    ]
  }
				],
				mainActiveIndex: 0,
				activeId: 1,
				city1: [{
				  text: '杭州',
				  id: 1
				}, {
				  text: '温州',
				  id: 2
				}, {
				  text: '宁波',
				  id: 3,
				  disabled: true
				}, {
				  text: '义乌',
				  id: 4
				}],
				city2: [{
				  text: '南京',
				  id: 5
				}, {
				  text: '无锡',
				  id: 6
				}, {
				  text: '徐州',
				  id: 7
				}, {
				  text: '苏州',
				  id: 8
				}],
				city3: [{
				  text: '泉州',
				  id: 9
				}, {
				  text: '厦门',
				  id: 10
				}]
			};
		},
		computed: {
// 			items() {
// 				return [
// 				{
// 					text: '所有',
// 					children: [this.city1, this.city2]
// 				},
// 				{
// 					text: '所有1',
// 					children: this.city1
// 				},
// 				{
// 					text: '所有2',
// 					children: this.city2
// 				},
// 				{
// 					text: '所有3',
// 					disabled: true,
// 					children: this.city3
// 				}
// 				];
// 			}
		},
		onLoad() {
			uni.request({
				url: msg.url(),
				method: 'GET',
				data: msg.get_all_quest_brief(this.$store.state.token),
				success: res => {
					console.log(res)
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
