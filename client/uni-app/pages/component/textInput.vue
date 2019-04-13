<template>
	<view @click="method">
		<el-autocomplete v-model="content" :fetch-suggestions="querySearch" @select="select" @blur="blur" :placeholder="placeholder" style="position: relative;top: -12upx;">
		</el-autocomplete>
		<!-- 用于触发数据同步与更新 -->
		<span style="display:none"> {{value}} </span>
	</view>
</template>

<script>
	import msg from '../../common/msg.js'
	
	export default {
		data() {
			return {
				Items:[],
				content: ''
			};
		},
		props:{
			//绑定数据
			datas:{
				type:Array,
				required:true
			},
			//输入为空时的提示
			placeholder:{
				type:String,
				required:true
			},
			method:{
				type:Function,
				required:true
			},
			select:{
				type:Function,
				required:true
			},
			value:{
				type:String,
				required:true
			}
		},
		updated: function() {
			this.content = this.value
		},
		methods: {
			loadAll() {
				if(this.datas.length === 0){
					console.log("load")
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.get_table_data(this.$store.state.token, this.tableName),
						success: res => {
							var items = res.data['data']
							for (let i = 0; i < items.length; i++) {
								var item = JSON.parse(items[i])
								var result = item[this.keys[0]]
								for(let j=1; j<this.keys.length;j++){
									result += ':' + item[this.keys[j]]
								}
								this.Items[i] = {
									value:result
								}
							}
						},
						fail: () => {
							
						},
						complete: () => {}
					});
				}
			},
			querySearch(queryString, cb) {
				var items = this.datas;
				var results = queryString ? items.filter(this.createStateFilter(queryString)) : items;
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
			blur(){
				if(this.value === ''){
					var item = new Object()
					item.value = ''
					this.select(item)
				}
			}
		}
	}
</script>

<style>
	view{
		height: 15upx;
	}
</style>
