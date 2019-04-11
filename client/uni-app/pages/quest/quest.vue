<template>
	<view>
		<el-container v-bind:style="{height: screenHeight}" v-if="dataLoadComplete" class="nav-bar" ref="navbar">
			
			<!-- 导航栏 -->
			<el-aside width="30%" style="background-color: rgb(238, 241, 246)">
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
						active-text-color="#ffd04b" 
						ref="menu"
						@open="handleOpen">
						<el-submenu v-for="index in items.length" :index='String(index)' >
							<template slot="title">
								<i class="el-icon-menu"></i>
								<span>{{items[index - 1]['text']}}</span>
								<span class="dot">{{items[index - 1].children.length > 999 ? "999+" : items[index - 1].children.length}}</span>
							</template>
							<el-menu-item-group v-for="subIndex in items[index - 1].children.length" :index="String(index)">
								<el-menu-item :index="String(index) + '.'+ String(subIndex) + '.' + items[index - 1].children[subIndex-1]['id']"
								 @click="onItemClick">
									{{items[index - 1].children[subIndex - 1]['text']}}
								</el-menu-item>
							</el-menu-item-group>
						</el-submenu>
					</el-menu>
				</el-scrollbar>
			</el-aside>
			
			<!-- 主内容 -->
			<el-container direction="vertical">
				<!-- 新增任务弹窗 -->
				<el-popover placement="left-start" width="800" trigger="click" v-model="isPopoverVisible">
					<el-card class="box-card">
						<view slot="header" class="clearfix">
							<span class="header">新增任务</span>
						</view>
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
					</el-card>
					<el-button slot="reference" type='primary' round class="float" @click="onAddQuestButtonClicked">新增任务</el-button>
				</el-popover>
				
				<el-main>
					<el-tabs v-model="activeTab" class="quest-details">
						<el-tab-pane :label='tabConfig[0]' :name='tabConfig[0]'>
							<quest-prop v-bind:tableRowData="currSelectedQuestData" v-bind:questTypes="questTypes" v-if="hasSelectedRowData"/>
						</el-tab-pane>
						<el-tab-pane :label='tabConfig[1]' :name='tabConfig[1]'>
							<quest-acpt v-bind:tableRowData="currSelectedQuestData" v-if="hasSelectedRowData"/>
						</el-tab-pane>
						<el-tab-pane :label='tabConfig[2]' :name='tabConfig[2]'>
							<quest-goal v-bind:tableRowData="currSelectedQuestData" v-if="hasSelectedRowData"/>
							</el-tab-pane>
						<el-tab-pane :label='tabConfig[3]' :name='tabConfig[3]'>
							<quest-comp v-bind:tableRowData="currSelectedQuestData" v-if="hasSelectedRowData"/>
							</el-tab-pane>
					</el-tabs>
				</el-main>
			</el-container>
		</el-container>

	</view>
</template>

<script>
	import msg from '../../common/msg.js'
	import config from '../../common/config.js'
	
	import questProp from './quest-prop.vue'
	import questAcpt from './quest-acpt.vue'
	import questGoal from './quest-goal.vue'
	import questComp from './quest-comp.vue'
	
	export default {
		data() {
			return {
				screenHeight: '',
				tabConfig: [],
				items: [],
				mainActiveIndex: 0,
				activeSn: 0,
				activeTab: 0,
				dataLoadComplete: false,
				hasSelectedRowData: false,
				questTypes: [],
				occupiedQuestSNs: [],
				currSelectedQuestData: null,
				navmenuSearchOptions: [],
				searchedMenuItem: null,
				currentActivatedIndex: '',
				
				/* 新增任务用 */
				selectedAddQuestType: 0,
				inputAddQuestSN: 0,
				inputAddQuestName: '',
				isAddQuestSNValid: true,
				isPopoverVisible: false
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
			// 保持container高度为屏幕尺寸的93%
			this.screenHeight = "93vh"
			
			// 更新滚动条位置
			if (this.currentActivatedIndex != '') {
				this.$refs.scrollBar.wrap.scrollTop = this.getScrollPosition(this.currentActivatedIndex)
			}
		},
		onLoad: function() {
			// 任务页签的配置
			this.tabConfig = config.ExcelConfig()['QuestTab']
			this.onLoadQuestBrief()
		},
		methods: {
			handleOpen: function(key, keyPath) {
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
			triggerItemClick: function(index) {
				// 解析点击项ID
				var splittedIndex = index.split('.')
				this.currentActivatedIndex = index
				this.activeSn = splittedIndex[2]
				
				// 打开对应顶级菜单
				this.openSelectedMenu(splittedIndex[0])
				
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_table_data_by_sn(this.$store.state.token, 'QUEST', this.activeSn),
					success: res => {
						this.currSelectedQuestData = JSON.parse(res.data['data'])
						this.hasSelectedRowData = true
					},
					fail: res => {
						uni.showToast({
							title: '获取不到对应的任务数据！',
							icon: 'none',
							duration: 2000
						});
					}
				});
			},
			onLoadQuestBrief: function() {
				uni.request({
					url: msg.url(),
					method: 'GET',
					data: msg.get_all_quest_brief(this.$store.state.token),
					success: res => {
						this.items = res.data['data']
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
						
						this.activeTab = this.tabConfig[0]
						this.dataLoadComplete = true
						
						this.formatSearchOptions();
					}
				});
			},
			formatSearchOptions: function() {
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
			openSelectedMenu: function(activatedIndex) {
				// 关闭之前展开的顶级菜单，并保留当前激活项目对应的顶级菜单
				for (var i = 1; i <= this.items.length; i++) {
					if (i != activatedIndex) {
						this.$refs.menu.close(String(i))
					}
					else {
						this.$refs.menu.open(String(i))
					}
				}
			},
			getScrollPosition: function(activatedIndex) {
				// 每个主菜单项目的高度为56px
				// 每个子菜单项目的高度为64px(50px height + 14px item_group_title height)
				// 每页（对齐后）有11个子菜单项目，也即如果要保持激活菜单项在中间位置的话，其前面应该有4或5个菜单项
				console.log("Activated Index: " + activatedIndex)
				const topLevelItemHeight = 56
				const subLevelItemHeight = 64
				var splittedIndex = activatedIndex.split('.')
				var scrollPos = Math.max(0, (splittedIndex[0]) * topLevelItemHeight + (splittedIndex[1] - 1) * subLevelItemHeight - 4 * subLevelItemHeight)
				return scrollPos
			},
			generateNextQuestID: function(questIndex) {
				// 获取当前最大的任务ID
				var occupiedSNs = this.occupiedQuestSNs[questIndex]['sn']
				this.inputAddQuestSN = occupiedSNs[occupiedSNs.length - 1] + 1
			},
			onAddQuestButtonClicked: function() {
				this.resetAddQuestForm()
			},
			onAddQuestIDUpdated: function() {
				// 检查输入的任务ID是否合法
				if (this.occupiedQuestSNs[this.selectedAddQuestType]['sn'].indexOf(this.inputAddQuestSN) > -1) {
					this.isAddQuestSNValid = false
				}
				else {
					this.isAddQuestSNValid = true
				}
			},
			onAddQuestTypeChanged: function() {
				this.onAddQuestIDUpdated()
			},
			resetAddQuestForm: function() {
				// 设置默认新增的任务类型
				this.selectedAddQuestType = 0
				
				// 找到默认任务类型的下一个可用任务ID
				this.generateNextQuestID(0)
				
				// 清空描述域
				this.inputAddQuestName = ''
			},
			submitAddQuestForm: function() {
				console.log("提交新增任务记录请求")
				
				// 隐藏弹窗
				this.isPopoverVisible = false
				
				// 刷新页面
				
				// 激活对应的任务
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
		position:fixed;
		margin-top: 5upx;
		top:30upx;
		right:30upx;
		text-align:center;
		box-shadow: 2px 2px 3px #999;
		z-index: 10;
	}
	
	.clearfix {
		  font-size: 12upx;
		  font-weight: bold
	}
</style>
