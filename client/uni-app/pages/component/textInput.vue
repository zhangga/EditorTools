<template>
	<view @click="method">
		<el-autocomplete v-model="content" :fetch-suggestions="querySearch" @select="select" :placeholder="placeholder" style="position: relative;top: -12upx;">
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
				content: '',
				prevContent: '',
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
			},
		},
		updated: function() {
			if (this.prevContent == this.content) {
				console.log("prevContent == content")
				if (this.content != '') {
					this.content = this.value;
					this.prevContent = this.content;
				}
			} else {
				this.prevContent = this.content
				console.log("当前content为：" + this.content)
				
				if(this.content === ''){
					console.log("保存空值")
					var item = new Object()
					item.value = ''
					this.select(item)
				}
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
			}
		}
	}
</script>

<style>
	view{
		height: 15upx;
	}
</style>
