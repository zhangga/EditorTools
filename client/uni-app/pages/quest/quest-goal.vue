<template>
	<view class="content">
		<el-row :gutter="12">
			<el-col :span="8">
				<el-card shadow="hover">
					<el-checkbox v-model="canAutomaticDeliver">【可交】后自动完成</el-checkbox>
				</el-card>
			</el-col>
		</el-row>
		<el-row :gutter="12">
			<el-col :span="20">
				<el-card shadow="hover" class="box-card">
					<div slot="header" class="clearfix">
						<span>任务目标</span>
						<el-switch style="display: block; float: right; padding-top: 20upx; padding-right: 20upx"
							v-model="goalNull" @change="onGoalNullChange"
							active-color="#ff4949" inactive-color="#13ce66"
							active-text="无目标" inactive-text="有目标">
						</el-switch>
					</div>
				</el-card>
			</el-col>
		</el-row>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				canAutomaticDeliver: false,
				goalNull: true,
			};
		},
		methods: {
			onGoalNullChange: function(e) {
				console.log(e)
			}
		}
	}
</script>

<style>
  .content {
	padding-top: 30upx;
	padding-left: -20upx;
  }
  .el-row {
	margin-bottom: 10upx;
	&:last-child {
	  margin-bottom: 0;
	}
  }
  .box-card {
    width: 400upx;
  }
</style>
