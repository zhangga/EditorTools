<template>
	<view @click="method">
		<el-autocomplete v-model="value" :fetch-suggestions="querySearch" @select="select" :placeholder="placeholder">
		</el-autocomplete>
	</view>
</template>

<script>
	import msg from '../../common/msg.js'
	export default {
		data() {
			return {
				Items:[],
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
			handleSelect(item) {
				this.$emit("",item.value)
			}
		}
	}
</script>

<style>
	view{
		height: 30upx;
	}
</style>
