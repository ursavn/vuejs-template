import Vue from 'vue'
import App from './App.vue'
import router from '@/router'
import vuetify from '@/plugins/vuetify'
import '@/plugins'
import store from '@/store'
import { sync } from 'vuex-router-sync'
import axios from 'axios'
import VueAxios from 'vue-axios'
import moment from 'moment-timezone'
import VueMoment from 'vue-moment'
import i18n from './i18n'

Vue.prototype.liff = window.liff
Vue.config.productionTip = false

Vue.use(VueMoment, {
  moment,
})
Vue.filter('formatDate', function (value) {
  if (value) {
    return moment(String(value)).format(i18n.t('date_format.ja'))
  }
})
const user = localStorage.getItem('user_data')
if (user) {
  store.commit('user/logged', true)
  store.commit('user/user_data', JSON.parse(user))
}

axios.interceptors.request.use(config => {
  config.withCredentials = false
  let user = localStorage.getItem('user_data')
  if (user) {
    user = JSON.parse(user)
    if (user.token) {
      config.headers.Authorization = 'Bearer ' + user.token
    }
  }
  config.headers = {
    ...config.headers,
    'Content-Type': 'application/json;charset=utf-8',
    'Access-Control-Allow-Origin': 'http://localhost:8080',
    'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, DELETE',
    'Access-Control-Max-Age': 86400,
  }
  return config
})

Vue.use(VueAxios, axios)
sync(store, router)

new Vue({
  router,
  vuetify,
  store,
  i18n,
  render: h => h(App),
}).$mount('#app')
