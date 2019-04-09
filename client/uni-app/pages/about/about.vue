<template>
	<view>
		<el-carousel :interval="4000" type="card" height="250px">
			<el-carousel-item v-for="item in 6" :key="item">
				<h3>{{ item }}</h3>
			</el-carousel-item>
		</el-carousel>
		
		<div class="block">
			<el-timeline>
				<el-timeline-item v-for="index in vers.length" :timestamp="vers[index-1]['time']" placement="top">
					<el-card>
						<h4>{{ vers[index-1]['log'] }}</h4>
						<p>{{ vers[index-1]['name'] }} 提交于 {{ vers[index-1]['time'] }}</p>
					</el-card>
				</el-timeline-item>
			</el-timeline>
		</div>
	</view>
</template>

<script>
	import Ver from '../../common/version.js'
	export default {
		data() {
			return {
				vers: Ver.Vers()
			};
		},
		onLoad() {
		}
	}
</script>

<style>
	.el-carousel__item h3 {
		color: #475669;
		font-size: 14px;
		opacity: 0.75;
		line-height: 200px;
		margin: 0;
	}
	
	.el-carousel__item:nth-child(2n) {
		background-color: #99a9bf;
	}
	
	.el-carousel__item:nth-child(2n+1) {
		background-color: #d3dce6;
	}
	
	.block {
		width: 50%;
	}
</style>
