<template>
	<view class="content">
		<el-row :gutter="12">
			<el-col>
				<el-card shadow="hover" class="box-card1">
					<el-form label-width="100px">
						<el-form-item label="任务限时(秒)">
							<el-input placeholder="时间限制" v-model="timeLimit" clearable></el-input>
						</el-form-item>
						<el-form-item label="显示时间信息">
							<el-checkbox v-model="showTimeLimit" disabled></el-checkbox>
						</el-form-item>
					</el-form>
					<el-checkbox v-model="canAutomaticDeliver">【可交】后自动完成</el-checkbox>
				</el-card>
			</el-col>
		</el-row>
		<el-row :gutter="12">
			<el-col>
				<el-card shadow="hover" class="box-card2">
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
				timeLimit: 0,
				showTimeLimit: true
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
  }
  .el-row {
	margin-bottom: 10upx;
	&:last-child {
	  margin-bottom: 0;
	}
  }
  .box-card1 {
    width: 200upx;
  }
  .box-card2 {
    width: 400upx;
  }
</style>
