import Vue from 'vue'
import App from './App'

import pageHead from './components/page-head.vue'
import pageFoot from './components/page-foot.vue'

import store from './store'

import './element-ui/lib/theme-chalk/index.css';
import Pagination from './element-ui/lib/pagination.js';
import Dialog from './element-ui/lib/dialog.js';
import Autocomplete from './element-ui/lib/autocomplete.js';
import Dropdown from './element-ui/lib/dropdown.js';
import DropdownMenu from './element-ui/lib/dropdown-menu.js';
import DropdownItem from './element-ui/lib/dropdown-item.js';
import Menu from './element-ui/lib/menu.js';
import Submenu from './element-ui/lib/submenu.js';
import MenuItem from './element-ui/lib/menu-item.js';
import MenuItemGroup from './element-ui/lib/menu-item-group.js';
import Input from './element-ui/lib/input.js';
import InputNumber from './element-ui/lib/input-number.js';
import Radio from './element-ui/lib/radio.js';
import RadioGroup from './element-ui/lib/radio-group.js';
import RadioButton from './element-ui/lib/radio-button.js';
import Checkbox from './element-ui/lib/checkbox.js';
import CheckboxButton from './element-ui/lib/checkbox-button.js';
import CheckboxGroup from './element-ui/lib/checkbox-group.js';
import Switch from './element-ui/lib/switch.js';
import Select from './element-ui/lib/select.js';
import Option from './element-ui/lib/option.js';
import OptionGroup from './element-ui/lib/option-group.js';
import Button from './element-ui/lib/button.js';
import ButtonGroup from './element-ui/lib/button-group.js';
import Table from './element-ui/lib/table.js';
import TableColumn from './element-ui/lib/table-column.js';
import DatePicker from './element-ui/lib/date-picker.js';
import TimeSelect from './element-ui/lib/time-select.js';
import TimePicker from './element-ui/lib/time-picker.js';
import Popover from './element-ui/lib/popover.js';
import Tooltip from './element-ui/lib/tooltip.js';
import MessageBox from './element-ui/lib/message-box.js';
import Breadcrumb from './element-ui/lib/breadcrumb.js';
import BreadcrumbItem from './element-ui/lib/breadcrumb-item.js';
import Form from './element-ui/lib/form.js';
import FormItem from './element-ui/lib/form-item.js';
import Tabs from './element-ui/lib/tabs.js';
import TabPane from './element-ui/lib/tab-pane.js';
import Tag from './element-ui/lib/tag.js';
import Tree from './element-ui/lib/tree.js';
import Alert from './element-ui/lib/alert.js';
import Notification from './element-ui/lib/notification.js';
import Slider from './element-ui/lib/slider.js';
import Loading from './element-ui/lib/loading.js';
import Icon from './element-ui/lib/icon.js';
import Row from './element-ui/lib/row.js';
import Col from './element-ui/lib/col.js';
import Upload from './element-ui/lib/upload.js';
import Progress from './element-ui/lib/progress.js';
import Spinner from './element-ui/lib/spinner.js';
import Message from './element-ui/lib/message.js';
import Badge from './element-ui/lib/badge.js';
import Card from './element-ui/lib/card.js';
import Rate from './element-ui/lib/rate.js';
import Steps from './element-ui/lib/steps.js';
import Step from './element-ui/lib/step.js';
import Carousel from './element-ui/lib/carousel.js';
import Scrollbar from './element-ui/lib/scrollbar.js';
import CarouselItem from './element-ui/lib/carousel-item.js';
import Collapse from './element-ui/lib/collapse.js';
import CollapseItem from './element-ui/lib/collapse-item.js';
import Cascader from './element-ui/lib/cascader.js';
import ColorPicker from './element-ui/lib/color-picker.js';
import Transfer from './element-ui/lib/transfer.js';
import Container from './element-ui/lib/container.js';
import Header from './element-ui/lib/header.js';
import Aside from './element-ui/lib/aside.js';
import Main from './element-ui/lib/main.js';
import Footer from './element-ui/lib/footer.js';
import Timeline from './element-ui/lib/timeline.js';
import TimelineItem from './element-ui/lib/timeline-item.js';
import locale from 'element-ui/src/locale';
import CollapseTransition from 'element-ui/src/transitions/collapse-transition';

Vue.use(Pagination)
Vue.use(Dialog)
Vue.use(Autocomplete)
Vue.use(Dropdown)
Vue.use(DropdownMenu)
Vue.use(DropdownItem)
Vue.use(Menu)
Vue.use(Submenu)
Vue.use(MenuItem)
Vue.use(MenuItemGroup)
Vue.use(Input)
Vue.use(InputNumber)
Vue.use(Radio)
Vue.use(RadioGroup)
Vue.use(RadioButton)
Vue.use(Checkbox)
Vue.use(CheckboxButton)
Vue.use(CheckboxGroup)
Vue.use(Switch)
Vue.use(Select)
Vue.use(Option)
Vue.use(OptionGroup)
Vue.use(Button)
Vue.use(ButtonGroup)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(DatePicker)
Vue.use(TimeSelect)
Vue.use(TimePicker)
Vue.use(Popover)
Vue.use(Tooltip)
// Vue.use(MessageBox)
Vue.use(Breadcrumb)
Vue.use(BreadcrumbItem)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Tabs)
Vue.use(TabPane)
Vue.use(Tag)
Vue.use(Tree)
Vue.use(Alert)
// Vue.use(Notification)
Vue.use(Slider)
Vue.use(Loading)
Vue.use(Icon)
Vue.use(Row)
Vue.use(Col)
Vue.use(Upload)
Vue.use(Progress)
Vue.use(Spinner)
// Vue.use(Message)
Vue.use(Badge)
Vue.use(Card)
Vue.use(Rate)
Vue.use(Steps)
Vue.use(Step)
Vue.use(Carousel)
Vue.use(Scrollbar)
Vue.use(CarouselItem)
Vue.use(Collapse)
Vue.use(CollapseItem)
Vue.use(Cascader)
Vue.use(ColorPicker)
Vue.use(Transfer)
Vue.use(Container)
Vue.use(Header)
Vue.use(Aside)
Vue.use(Main)
Vue.use(Footer)
Vue.use(Timeline)
Vue.use(TimelineItem)
Vue.use(locale)
Vue.use(CollapseTransition)

import { DataTables, DataTablesServer } from './vue-data-tables';
Vue.use(DataTables)
Vue.use(DataTablesServer)

Vue.config.productionTip = false

Vue.prototype.$store = store
Vue.prototype.$backgroundAudioData = {
	playing: false,
	playTime: 0,
	formatedPlayTime: '00:00:00'
}

Vue.component('page-head', pageHead)
Vue.component('page-foot', pageFoot)

App.mpType = 'app'

const app = new Vue({
	store,
    ...App
})
app.$mount()
