import Vue from 'vue'
import { api } from '@/config/index'

// Pathify
import { make } from 'vuex-pathify'

const state = {
  order: {},
  list: [],
  message: '',
  status: '',
  totalOrders: '',
}

const mutations = make.mutations(state)

const actions = {
  fetch: ({ commit }, payload) => {
    commit('status', 'loading')

    let fullUrl = api.baseUrl + '/orders'
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
        commit('totalOrders', response.data.totalItems)
        commit('list', response.data)
      })
      .catch(() => {
        commit('status', '')
        commit('message', 'messages.server_error')
      })
  },
  get: ({ commit }, payload) => {
    commit('status', 'loading')
    const fullUrl = api.baseUrl + '/orders/' + payload.id
    Vue.axios
      .get(fullUrl)
      .then(response => {
        commit('status', '')
        commit('order', response.data)
      })
      .catch(() => {
        commit('status', '')
        commit('message', 'messages.server_error')
      })
  },
  // delete: ({ commit, dispatch }, payload) => {
  //   commit('status', 'loading')
  //   Vue.axios
  //     .delete(api.baseUrl + '/amazon/' + payload.id)
  //     .then(response => {
  //       commit('status', '')
  //       dispatch('fetch')
  //       commit('message', 'messages.updated_successfully')
  //     })
  //     .catch(error => {
  //       commit('status', '')
  //       if (error.response) {
  //         if (error.response.status === 400) {
  //           commit('message', 'messages.cant_login_amazon')
  //         }
  //       } else {
  //         commit('message', 'messages.server_error')
  //       }
  //     })
  // },
  clearMessage: ({ commit }) => {
    commit('message', '')
  },
  clearStatus: ({ commit }) => {
    commit('status', '')
  },
}

const getters = {}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
}
