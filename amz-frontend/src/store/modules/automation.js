import Vue from 'vue'
// Utilities
import { make } from 'vuex-pathify'
// Globals
import { api } from '@/config/index'
const state = {
  list: [],
  status: '',
  changed: '',
  message: '',
  addedProduct: {},
  currentActive: '',
  productStatus: false,
  logStatus: false,
  messageProduct: '',
  settingLog: [],
}

const mutations = make.mutations(state)

const actions = {
  getList: ({ commit }, payload) => {
    commit('status', 'loading')
    let fullUrl = api.baseUrl + '/automations'
    if (payload) {
      const query = new URLSearchParams(payload).toString()
      fullUrl += '?' + query
    }
    Vue.axios
      .get(fullUrl)
      .then(response => {
        commit('status', '')
        if (response.data.totalItems === 0) {
          commit('message', 'messages.list_empty')
        }
        commit('list', response.data)
      })
      .catch(error => {
        commit('status', '')
        if (error.response) {
          console.log('message', error.response.data.message)
        }
        commit('message', 'messages.server_error')
      })
  },
  fetchProduct: ({ commit }, payload) => {
    commit('productStatus', 'loading')
    commit('messageProduct', 'messages.finding_product')
    Vue.axios
      .get(api.baseUrl + '/products/' + payload.asin)
      .then(response => {
        commit('productStatus', '')
        commit('messageProduct', 'messages.completed')
        commit('addedProduct', response.data)
      })
      .catch(error => {
        commit('productStatus', error.message)
        if (error.response) {
          if (error.response.status === 404) {
            commit('messageProduct', 'messages.cant_find_product')
          } else {
            commit('messageProduct', 'messages.server_error')
          }
          console.log(error.response.data.message)
        } else {
          commit('messageProduct', 'messages.server_error')
        }
      })
  },
  create: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')

    Vue.axios
      .post(api.baseUrl + '/automations', payload)
      .then(response => {
        commit('status', '')
        commit('message', 'messages.save_successfully')
        dispatch('getList')
      })
      .catch(() => {
        commit('status', '')
        commit('message', 'messages.server_error')
      })
  },
  update: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')
    const options = payload.options
    delete payload.options
    Vue.axios
      .post(api.baseUrl + '/automations/' + payload.id, payload)
      .then(() => {
        commit('status', '')
        commit('message', 'messages.updated_successfully')
        dispatch('getList', options)
      })
      .catch(() => {
        commit('status', '')
        commit('message', 'messages.server_error')
      })
  },
  delete: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')
    const options = payload.options
    Vue.axios
      .delete(api.baseUrl + '/automations/' + payload.id)
      .then(() => {
        commit('status', '')
        commit('message', 'messages.item_deleted_successfully')
        dispatch('getList', options)
      })
      .catch(() => {
        commit('message', 'messages.server_error')
        commit('status', '')
      })
  },
  getCurrentActive: ({ commit }) => {
    commit('status', 'loading')
    const fullUrl = api.baseUrl + '/users'
    Vue.axios
      .get(fullUrl + '?size=100')
      .then(response => {
        const currentActive = response.data.data.filter(item => {
          return item.status === 'true'
        }).length
        commit('currentActive', currentActive)
        commit('message', 'error_500')
        commit('status', '')
      })
      .catch(error => {
        commit('status', '')
        commit('message', error.message)
      })
  },
  getSettingLog: ({ commit }, payload) => {
    commit('logStatus', 'loading')
    let fullUrl = api.baseUrl + '/automations/' + payload.id + '/logs'
    if (payload.options) {
      const query = new URLSearchParams(payload.options).toString()
      fullUrl += '?' + query
    }
    Vue.axios
      .get(fullUrl)
      .then(response => {
        commit('logStatus', '')
        if (response.data.totalItems === 0) {
          commit('message', 'messages.list_empty')
        }
        commit('settingLog', response.data)
      })
      .catch(error => {
        commit('logStatus', '')
        if (error.response) {
          console.log('message', error.response.data.message)
        }
        commit('message', 'messages.server_error')
      })
  },
  changeStatus: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')
    const fullUrl = api.baseUrl + '/automations/' + payload.id + '/status'
    const options = payload.options
    delete payload.id
    delete payload.options
    Vue.axios
      .post(fullUrl, payload)
      .then(response => {
        dispatch('getList', options)
        commit('status', '')
      })
      .catch(error => {
        commit('status', '')
        console.log(error.message)
        commit('message', 'messages.server_error')
      })
  },
  clearMessage: ({ commit }) => {
    commit('message', '')
  },
  clearProductMessage: ({ commit }) => {
    commit('messageProduct', '')
  },
  setMessage: ({ commit }, payload) => {
    commit('message', payload.message)
  },
}

const getters = {
  status: state => {
    return state.status
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
}
