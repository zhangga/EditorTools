<template>
	<view>
		<el-carousel :interval="3000" type="card" height="100upx">
			<el-carousel-item v-for="item in 5" :key="item">
				<view class="image-content">
                    <image style="width: 200px; height: 200px; background-color: #eeeeee;" src="/static/logo.png"
                        @error="imageError" v-if="item === 1"></image>
					<image style="width: 200px; height: 200px; background-color: #eeeeee;" src="/static/au_huangxiaoyi.png"
					    @error="imageError" v-if="item === 2"></image>
					<image style="width: 200px; height: 200px; background-color: #eeeeee;" src="/static/au_liguo.png"
					    @error="imageError" v-if="item === 3"></image>
					<image style="width: 200px; height: 200px; background-color: #eeeeee;" src="/static/au_yulixiang.png"
					    @error="imageError" v-if="item === 4"></image>
					<image style="width: 200px; height: 200px; background-color: #eeeeee;" src="/static/au_zhangga.png"
					    @error="imageError" v-if="item === 5"></image>
                </view>
			</el-carousel-item>
		</el-carousel>
		
		<div class="block">
			<el-timeline>
				<el-timeline-item v-for="tlIndex in vers.length" :timestamp="vers[tlIndex-1]['time']" placement="top">
					<el-card>
						<h4>{{ vers[tlIndex-1]['log'] }}</h4>
						<p>{{ vers[tlIndex-1]['name'] }} 提交于 {{ vers[tlIndex-1]['time'] }}</p>
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
