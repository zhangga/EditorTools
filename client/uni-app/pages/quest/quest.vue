<template>
	<view ref="quest">
		<vue-canvas-nest :config="{color:'255,0,0', opacity:'0.8'}"></vue-canvas-nest>
		<el-container v-bind:style="{height: screenHeight}" v-if="dataLoadComplete" class="nav-bar" ref="navbar">
			<!-- 导航栏 -->
			<el-aside width="30%" style="background-color: rgb(76,76,76)">
				<el-header height="80px">
					<el-row>
						<el-col :span="8">
							<span>任务菜单</span>
						</el-col>
						<el-col :span="15">
							<el-cascader :options="navmenuSearchOptions" placeholder="按照名称/ID搜索" 
							filterable v-model="searchedMenuItem" style="position: absolute; right: 10upx"
							@change="handleSearchChanges"></el-cascader>
						</el-col>
					</el-row>
				</el-header>
				<el-scrollbar ref="scrollBar" style="height: 100%;">
					<el-menu
						background-color="#4C4C4C"
						active-text-color="#ffd04b" 
						text-color="#fff"
						ref="menu"
						@open="handleOpen">
						<el-submenu v-for="index in items.length" :index='String(index)'>
							<template slot="title">
								<i class="el-icon-menu"></i>
								<span>{{items[index - 1]['text']}}</span>
								<span class="dot">{{items[index - 1].children.length > 999 ? "999+" : items[index - 1].children.length}}</span>
							</template>
							<el-menu-item-group v-for="(subItem, subIndex) in items[index - 1].children">
								<el-menu-item :index="String(index) + '.'+ String(subIndex + 1) + '.' + subItem['id']"
								 @click="onItemClick">
									{{subItem['text']}}
								</el-menu-item>
							</el-menu-item-group>
						</el-submenu>
					</el-menu>
				</el-scrollbar>
			</el-aside>
			
			<!-- 主内容 -->
			<el-container direction="vertical">
				<!-- 新增任务弹窗 -->
				<el-button type='primary' round class="float" @click="onAddQuestButtonClicked" icon="el-icon-circle-plus">新增任务</el-button>
				<el-dialog title="新增任务" width="50%" :visible.sync="showAddQuestDialog" center style="line-height: 0.5">
					<el-form label-width="60upx">
						<el-form-item label="任务类型">
							<el-select v-model="selectedAddQuestType" size="medium" id="questType" @change="onAddQuestTypeChanged">
								<el-option v-for="index in questTypes.length" :key="index" :value="index - 1" :label="questTypes[index - 1]"></el-option>
							</el-select>
						</el-form-item>
						<el-form-item label="任务SN">
							<el-input-number v-model="inputAddQuestSN" :min="0" id="addQuestSN" @change="onAddQuestIDUpdated"></el-input-number>
							<span style="margin-left: 10upx; color: #DD524D;" v-if="inputAddQuestSN != '' && !isAddQuestSNValid">输入的任务ID已被占用！</span>
						</el-form-item>
						<el-form-item label="任务名称">
							<el-input v-model="inputAddQuestName" placeholder="请输入String类型任务名称(对应questName)" id="questName">
							</el-input>
						</el-form-item>
						<view style="display: table; clear:both; width: 100%;">
							<el-button type='primary' style="float: left" @click="resetAddQuestForm">重置</el-button>
							<el-button type='primary' style="float: right;" @click="submitAddQuestForm">确认</el-button>
						</view>
					</el-form>
				</el-dialog>
				
				<!-- 刷新按钮 -->
				<el-button type='success' circle class="float_refresh" icon="el-icon-refresh" @click="onRefreshBtnClicked"></el-button>
				
				<!-- 提交SVN按钮 -->
				<el-button type='primary' round class="float_submit" icon="el-icon-upload" @click="onSubmitToSVNBtnClicked">提交至SVN</el-button>
				
				<el-main>
					<el-tabs v-model="activeTab" value="activeTab" class="quest-details" @tab-click="onTabClicked">
						<el-tab-pane :label='tabConfig[0].value' :name='tabConfig[0].key'>
							<quest-prop 
								v-bind:tableRowData="currSelectedQuestData" 
								v-bind:questTypes="questTypes" 
								v-bind:allTableData="items"
								v-bind:currTableName="currentTableName" 
								v-if="hasSelectedRowData"/>
						</el-tab-pane>
						<el-tab-pane :label='tabConfig[1].value' :name='tabConfig[1].key'>
							<quest-acpt 
								v-bind:tableRowData="currSelectedQuestData" 
								v-bind:allTableData="items" 
								v-if="hasSelectedRowData"/>
						</el-tab-pane>
						<el-tab-pane :label='tabConfig[2].value' :name='tabConfig[2].key'>
							<quest-goal 
								v-bind:tableRowData="currSelectedQuestData" 
								v-if="hasSelectedRowData"/>
							</el-tab-pane>
						<el-tab-pane :label='tabConfig[3].value' :name='tabConfig[3].key'>
							<quest-comp 
								v-bind:tableRowData="currSelectedQuestData" 
								v-if="hasSelectedRowData"/>
							</el-tab-pane>
					</el-tabs>
				</el-main>
			</el-container>
		</el-container>
		
		<el-container v-if="isSessionTimeout">
			<span class="timeoutHint">当前用户Session已过期，请重新登录！</span>
		</el-container>

	</view>
</template>

<script>
	import msg from '../../common/msg.js'
	import config from '../../common/config.js'
	import util from '../../common/util.js'
	
	import questProp from './quest-prop.vue'
	import questAcpt from './quest-acpt.vue'
	import questGoal from './quest-goal.vue'
	import questComp from './quest-comp.vue'
	
	export default {
		data() {
			return {	
				/* 本地数据缓存 */
				items: [],
				tabConfig: [],
				questTypes: [],
				occupiedQuestSNs: [],
				navmenuSearchOptions: [],
				
				/* 新增任务用 */
				selectedAddQuestType: 0,
				inputAddQuestSN: 0,
				inputAddQuestName: '',
				isAddQuestSNValid: true,
				
				/* 状态描述用 */
				dataLoadComplete: false,
				activeSn: 0,
				activeTab: 0,
				mainActiveIndex: 0,
				currSelectedQuestData: null,
				currentActivatedIndex: '',
				searchedMenuItem: null,
				hasSelectedRowData: false,
				isSessionTimeout: false,
				showAddQuestDialog: false,
				
				/* 全局变量 */
				screenHeight: '',
				currentTableName: '',
				loadingInstance: null,					// 加载中（loading）动画实例
			};
		},
		computed: {
		},
		components: {
			questProp,
			questAcpt,
			questGoal,
			questComp
		},
		updated: function() {
			// 保持container高度为屏幕尺寸的100%
			this.screenHeight = "100vh"
			
			// 更新滚动条位置
			if (this.currentActivatedIndex != '') {
				this.$refs.scrollBar.wrap.scrollTop = this.getScrollPosition(this.currentActivatedIndex)
			}
		},
		onLoad: function(option) {
			// 接受页面跳转传递的参数（uni-app跳转传参）
			this.currentTableName = option.table_name
			
			// 任务页签的配置
			this.tabConfig = config.ExcelConfig()['QuestTab']
			this.onLoadQuestBrief()
		},
		methods: {
			handleOpen: function(key, keyPath) {
			},
			onTabClicked: function(currTab) {
				switch(currTab.name) {
					case this.tabConfig[0].key:						// 任务属性页
						break;
					case this.tabConfig[1].key:						// 任务接取页
						break;
					case this.tabConfig[2].key:						// 任务目标页
						
						break;
					case this.tabConfig[3].key:						// 任务完成页
						
						break;
				}
			},
			handleSearchChanges: function(value) {
				if (value.length >= 1) {
					this.openSelectedMenu(value[0])
				}
				
				if (value.length >= 2) {
					this.$refs.menu.activeIndex = value[1]
					this.triggerItemClick(value[1])
				}
			},
			onNavClick: function(index) {
				this.mainActiveIndex = index;
			},
			onItemClick: function(e) {
				this.triggerItemClick(e.index)
			},
			triggerItemClick: function(index, shouldPerformIndexCheck = false, callback = null, ...params) {
				// 检测当前提供的index是否仍然存在
				if (shouldPerformIndexCheck && !this.doesItemIndexValid(index)) {
					// 如果不存在，默认打开第一个index对应的菜单项
					index = "1.1." + this.items[0].children[0].id
				}
				
				// 更新当前激活记录的ID
				this.currentActivatedIndex = index
				
				if (index == '') {
					return
				}
				
				// 解析点击项ID
				let splittedIndex = index.split('.')
				this.activeSn = splittedIndex[2]
				
				// 打开对应顶级菜单
				this.openSelectedMenu(splittedIndex[0])
				
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_table_data_by_sn(util.getCurrentUserToken(), 'QUEST', this.activeSn),
					success: res => {
						this.currSelectedQuestData = JSON.parse(res.data['data'])
						this.hasSelectedRowData = true
						
						// 更新页面导航栏文字
						uni.setNavigationBarTitle({
							title: "当前操作任务：" + this.currSelectedQuestData['questName']
						});
						
						// 更新版本号缓存信息
						this.$store.state.verNum.set(this.currentTableName, this.currSelectedQuestData['verNum'])
						
						// 执行回调方法
						if (callback != null) {
							if (params.length == 1) {
								callback(params[0])
							}
							else if (params.length == 2) {
								callback(params[0], params[1])
							}
						}
					},
					fail: res => {
						this.$notify.error({
							title: '获取失败',
							message: '获取不到对应的任务数据！'
						});
					}
				});
			},
			onLoadQuestBrief: function(callback = null) {
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_all_quest_brief(util.getCurrentUserToken()),
					success: res => {
						this.items = res.data['data']
						this.occupiedQuestSNs = []
						this.questTypes = []
						
						if (this.items == null) {
							this.isSessionTimeout = true;
							return;
						}
						
						// 排序
						for (let i = 0; i < this.items.length; i++) {
							this.questTypes.push(this.items[i]['text'])
							let children = this.items[i]['children']
							children.sort(function(a, b) {
								return a['id'] - b['id']
							});
							
							var occupiedSN = {"type": this.items[i]['text'], 'sn': []}
							for (var j = 0; j < this.items[i]['children'].length; j++) {
								occupiedSN['sn'].push(this.items[i]['children'][j]['id'])
							}
							
							this.occupiedQuestSNs.push(occupiedSN)
						}
						
						this.activeTab = this.tabConfig[0].key
						this.dataLoadComplete = true
						
						// 预处理操作，用于任务搜索
						this.formatSearchOptions();
						
						// 执行回调方法
						if (callback != null) {
							callback()
						}
					}
				});
			},
			formatSearchOptions: function() {
				// 初始化
				this.navmenuSearchOptions = []
				
				if (this.items.length == 0) {
					return
				}
				
				for (var i = 0; i < this.items.length; i++) {
					var topLevelOption = {"value": '', "label": '', "children": []}
					topLevelOption['value'] = String(i + 1)
					topLevelOption['label'] = this.items[i]['text']
					
					for (var j = 0; j < this.items[i].children.length; j++) {
						var lowLevelOption = {"value": '', "label": ''}
						lowLevelOption['value'] = String(i + 1) + '.' + String(j + 1) + '.' + this.items[i].children[j]['id']
						lowLevelOption['label'] = this.items[i].children[j]['text']
						
						topLevelOption['children'].push(lowLevelOption)
					}
					
					if (topLevelOption['children'].length == 0) {
						delete topLevelOption.children
					}
					
					this.navmenuSearchOptions.push(topLevelOption)
				}
			},
			// 关闭之前展开的顶级菜单，并保留当前激活项目对应的顶级菜单
			openSelectedMenu: function(activatedIndex) {
				for (let i = 1; i <= this.items.length; i++) {
					if (i != activatedIndex) {
						this.$refs.menu.close(String(i))
					}
					else {
						this.$refs.menu.open(String(i))
					}
				}
			},
			// 检查提供的index是否仍存在（可能已被删除）
			doesItemIndexValid: function(itemIndex) {
				// 解析点击项ID
				let splittedIndex = itemIndex.split('.')
				let itemSn = splittedIndex[2]
				
				for (let i = 0; i < this.items.length; i++) {
					let subItems = this.items[i].children
					for (let j = 0; j < subItems.length; j++) {
						if (itemSn == subItems[j].id) {
							return true
						}
					}
				}
				
				return false
			},
			getScrollPosition: function(activatedIndex) {
				// 每个主菜单项目的高度为56px
				// 每个子菜单项目的高度为64px(50px height + 14px item_group_title height)
				// 每页（对齐后）有11个子菜单项目，也即如果要保持激活菜单项在中间位置的话，其前面应该有4或5个菜单项
				const topLevelItemHeight = 56
				const subLevelItemHeight = 64
				var splittedIndex = activatedIndex.split('.')
				var scrollPos = Math.max(0, (splittedIndex[0]) * topLevelItemHeight + (splittedIndex[1] - 1) * subLevelItemHeight - 4 * subLevelItemHeight)
				return scrollPos
			},
			generateNextQuestID: function(questIndex) {
				// 获取当前最大的任务ID
				var occupiedIDbyQuest = this.occupiedQuestSNs[0]['sn']
				var maxSn = parseInt(occupiedIDbyQuest[occupiedIDbyQuest.length - 1])
				
				for (var i = 1; i < this.questTypes.length; i++) {
					occupiedIDbyQuest = this.occupiedQuestSNs[i]['sn']
					maxSn = Math.max(maxSn, occupiedIDbyQuest.length == 0 ? 0 : parseInt(occupiedIDbyQuest[occupiedIDbyQuest.length - 1]))
				}
				
				this.inputAddQuestSN = maxSn + 1
			},
			onRefreshBtnClicked: function() {
				var cachedActivatedTab = this.activeTab
				this.onLoadQuestBrief(() => {
					util.isQuestDataExpired = true
					// 刷新当前选择的记录的内容以及版本号
					this.triggerItemClick(this.currentActivatedIndex, true, (previousActiveTab) => {
						// 激活刷新前正在查看的tab
						this.activeTab = previousActiveTab;
						console.log("当前激活的tab：" + cachedActivatedTab);
					}, cachedActivatedTab)
				})
			},
			onSubmitToSVNBtnClicked: function() {
				this.$confirm('确定提交更改至SVN吗？', '操作确认', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					uni.request({
						url: msg.url(),
						method: 'GET',
						data: msg.submit_to_svn(util.getCurrentUserToken()),
						success: res => {
							this.$message({
								type: 'success',
								message: '操作成功'
							});
						}
					})
				}).catch(() => { 
				});
			},
			onAddQuestButtonClicked: function() {
				this.showAddQuestDialog = true
				this.resetAddQuestForm()
			},
			onAddQuestIDUpdated: function() {
				// 检查输入的任务ID是否合法
				for (var i = 0; i < this.questTypes.length; i++) {
					if (this.occupiedQuestSNs[i]['sn'].indexOf(this.inputAddQuestSN) > -1) {
						this.isAddQuestSNValid = false
						return
					}
				}
				this.isAddQuestSNValid = true
			},
			onAddQuestTypeChanged: function() {
				// this.isAddQuestSNValid = (this.occupiedQuestSNs[this.selectedAddQuestType]['sn'].indexOf(this.inputAddQuestSN) == -1)
			},
			resetAddQuestForm: function() {
				// 设置默认新增的任务类型
				this.selectedAddQuestType = 0
				
				// 找到下一个可用任务ID
				this.generateNextQuestID()
				
				// 清空描述域
				this.inputAddQuestName = ''
			},
			submitAddQuestForm: function() {
				// 检查数据
				var errorMsg = ''
				if (this.selectedAddQuestType > 3 || this.selectedAddQuestType < 0) {
					errorMsg = '请检查选择的任务类型'
				}
				if (!this.isAddQuestSNValid) {
					errorMsg = errorMsg == '' ? '请输入合法的任务ID' : errorMsg + '，输入的任务ID'
				}
				
				if (this.inputAddQuestName == '') {
					errorMsg = errorMsg == '' ? '请输入合法的任务名称' : errorMsg + '及任务名称'
				}
				
				if (errorMsg != '') {
					this.$message({
						showClose: true,
						message: errorMsg,
						type: 'error'
					});
				}
				else {
					// 显示loading界面
					 this.loadingInstance = this.$loading({
						 lock: true,
						 text: "新增记录中...",
						 spinner: 'el-icon-loading',
						 background: 'rgba(0, 0, 0, 0.7)'
					 })
					 
					// 提交表单
					var addKeyValues = "\{\"questType\":" + parseInt(this.selectedAddQuestType + 1) 
										+ ",\"questName\":\"" + this.inputAddQuestName 
										+ "\",\"sn\":" + this.inputAddQuestSN + "\}"
										
					util.addDataField(this.currentTableName, addKeyValues, this, this.onFinishedAddingQuest)
				}
			},
			onFinishedAddingQuest: function() {
				this.loadingInstance.close()
			},
			onAddTableData: function(replyData) {
				// 隐藏弹窗
				this.showAddQuestDialog = false
				
				// 刷新页面
				this.onLoadQuestBrief(() => {
					// 计算当前加入的任务在菜单中对应的index
					let json = JSON.parse(replyData)
					let questSn = json['sn']
					let questType = json['questType']
					let usedSNs = this.occupiedQuestSNs[parseInt(questType) - 1]['sn']
					
					let index = String(questType) + '.' + parseInt(usedSNs.indexOf(parseInt(questSn)) + 1) + '.' + questSn
					
					// 更新任务激活状态显示
					this.$refs.menu.activeIndex = index
					
					// 触发任务点击事件
					this.triggerItemClick(index)
					
				})
			}
		}
	}
</script>

<style>
	.nav-bar {
		height: 100%;
		border: 1px solid #eee
	}
	
	.quest-details {
		margin-left: 3%;
		width: 94%;
		margin-left: 3%;
	}
	
	.dot {
		background:#409EFF; 
		width: 22px;
		height: 22px;
		border-radius: 50%; 
		
		font-size: 8px; 
		color: #FFFFFF;
		text-align: center;
		line-height: 22px;
		
		position: absolute;
		top: 30%;
		right: 20%;
	}
	
	.el-header {
		background-color: #909399;
		color: #333;
		/* line-height: 60px; */
		font-size: 25px;
	}
	
	.el-row {
		padding: 3%
	}
	
	.el-scrollbar__wrap {
		overflow-x: hidden;
	}
	
	.el-container .el-aside {
		/* 隐藏系统滚动条 */
		overflow: hidden
	}
	
	.float {
		position: fixed;
		top: 8%;
		right: 9%;
		text-align: center;
		box-shadow: 2px 2px 3px #999;
		z-index: 10;
	}
	
	.float_refresh {
		position: fixed;
		top: 8%;
		right: 5%;
		box-shadow: 2px 2px 3px #999;
		z-index: 10;
	}
	
	.float_submit {
		position: fixed;
		bottom: 8%;
		right: 5%;
		box-shadow: 2px 2px 3px #999;
		z-index: 10;
	}
	
	.clearfix {
		  font-size: 10upx;
		  font-weight: bold;
	}
	
	.timeoutHint {
		width: 180upx;
		height: 20upx;
		font-size: 15upx; 
		text-align: center;
		position: absolute;
		top: 50%;
		left: 50%;
		margin-left: -90upx;
		margin-top: -120upx;
	}
</style>
