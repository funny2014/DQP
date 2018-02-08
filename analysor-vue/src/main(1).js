import Vue from 'vue'
import App from './App'
import router from './router'

import ElementUI from 'element-ui'
import VueCodeMirror from 'vue-codemirror'
import './assets/theme/theme-darkblue/index.css'


Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(VueCodeMirror)




new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
