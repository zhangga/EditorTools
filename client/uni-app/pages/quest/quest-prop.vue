<template>
	<view>
		<el-card class="box-card">
			<view slot="header" class="clearfix">
				<span class="header">任务属性</span>
			</view>
			<el-form ref="form" :model="form" label-width="100px">
				<el-form-item label="任务名称">
					<el-input placeholder="请输入String类型名称" class="text"></el-input>
				</el-form-item>
				<el-form-item label="任务描述">
					<el-input placeholder="请输入String类型描述" type="textarea" :rows="2" class="text"></el-input>
				</el-form-item>
			</el-form>
		</el-card>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			};
		}
	}
</script>

<style>
  .text {
      font-size: 14px;
  }
  
  .header {
      font-size: 50px;
  }

  .item {
      margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
	  display: table;
      content: "";
  }
  .clearfix:after {
	  clear: both
  }

  .box-card {
	  width: 480px;
  }
</style>
