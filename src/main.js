import Vue from 'vue'
import App from './App.vue'
//引入VueRouter
import VueRouter from 'vue-router'
//引入路由器
import router from './router/index'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
Vue.config.productionTip = false

Vue.use(VueRouter)
Vue.use(ElementUI);
import Icon from 'vue-svg-icon/Icon.vue'


Vue.config.productionTip = false

Vue.component('icon',Icon)


new Vue({
  render: h => h(App),
  router
}).$mount('#app')
