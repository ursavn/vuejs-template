import Vue from 'vue'
import { api } from '@/config/index'

// Pathify
import { make } from 'vuex-pathify'

const state = {
  list: [],
  message: '',
  status: '',
  totalAccounts: '',
  pollingStatus: '',
  captchaStatus: '',
  captchaMessage: '',
  captchaResponse: '',
  otpStatus: '',
  otpMessage: '',
  creating: {},
}

const mutations = make.mutations(state)

const actions = {
  fetch: ({ commit }, payload) => {
    commit('status', 'loading')

    let fullUrl = api.baseUrl + '/amazon'
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
        commit('totalAccounts', response.data.totalItems)
        commit('list', response.data)
      })
      .catch(() => {
        commit('status', '')
        commit('message', 'messages.server_error')
      })
  },
  create: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')
    Vue.axios
      .post(api.baseUrl + '/amazon', payload)
      .then(response => {
        commit('status', response.data.status)
        commit('creating', response.data)
        if (response.data.status === 'ERROR') {
          commit('message', 'messages.cant_login_amazon')
          return
        }

        commit('message', 'messages.save_successfully')
        dispatch('fetch')
      })
      .catch(error => {
        commit('status', '')
        if (error.response) {
          if (error.response.status === 400) {
            console.log(error.response.data.message)
            if (error.response.data.message === 'Error: Email is existed!') {
              commit('message', 'messages.email_existed')
            } else commit('message', 'messages.cant_login_amazon')
          }
        } else {
          commit('message', 'messages.server_error')
        }
      })
  },
  createPolling: ({ commit }, payload) => {
    commit('pollingStatus', 'polling')
    Vue.axios
      .get(api.baseUrl + '/amazon/' + payload.id)
      .then(response => {
        commit('pollingStatus', response.data)
      })
      .catch(error => {
        if (error.response) {
          if (error.response.status === 400) {
            commit('pollingStatus', 'messages.cant_login_amazon')
          }
        } else {
          commit('pollingStatus', 'messages.server_error')
        }
      })
  },
  reCreate: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')
    Vue.axios
      .post(api.baseUrl + '/amazon/' + payload.id + '/relogin')
      .then(response => {
        commit('status', response.data.status)
        commit('creating', response.data)
        commit('message', 'messages.verify_your_account')
        dispatch('fetch')
      })
      .catch(error => {
        commit('status', '')
        if (error.response) {
          if (error.response.status === 400) {
            commit('message', 'messages.cant_login_amazon')
          }
        } else {
          commit('message', 'messages.server_error')
        }
      })
  },
  edit: ({ commit }, payload) => {
    const fcallback = payload.callback
    fcallback('abc', true)
  },
  delete: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')
    const options = payload.options
    Vue.axios
      .delete(api.baseUrl + '/amazon/' + payload.id)
      .then(response => {
        commit('status', '')
        dispatch('fetch', options)
        commit('message', 'messages.updated_successfully')
      })
      .catch(error => {
        commit('status', '')
        if (error.response) {
          if (error.response.status === 400) {
            commit('message', 'messages.cant_login_amazon')
          }
        } else {
          commit('message', 'messages.server_error')
        }
      })
  },
  sendCaptcha: ({ commit, dispatch }, payload) => {
    commit('captchaStatus', 'loading')
    Vue.axios
      .post(api.baseUrl + '/amazon/captcha', payload)
      .then(response => {
        commit('captchaStatus', '')
        if (response.data.status === 'SUCCESS') dispatch('fetch')
        if (response.data.status === 'ERROR') {
          commit('captchaMessage', 'messages.cant_login_amazon')
          return
        }
        commit('captchaResponse', response.data)
        commit('captchaMessage', 'messages.save_successfully')
      })
      .catch(error => {
        commit('captchaStatus', '')
        if (error.response) {
          if (error.response.status === 400) {
            commit('captchaMessage', 'messages.cant_login_amazon')
          }
        } else {
          commit('captchaMessage', 'messages.server_error')
        }
      })
  },
  sendOtp: ({ commit, dispatch }, payload) => {
    commit('otpStatus', 'loading')
    Vue.axios
      .post(api.baseUrl + '/amazon/captcha', payload)
      .then(response => {
        commit('otpStatus', '')
        if (response.data.status === 'SUCCESS') dispatch('fetch')
        if (response.data.status === 'ERROR') {
          commit('otpMessage', 'messages.cant_login_amazon')
          return
        }
        commit('otpMessage', 'messages.save_successfully')
      })
      .catch(error => {
        commit('otpStatus', '')
        if (error.response) {
          if (error.response.status === 400) {
            commit('otpMessage', 'messages.cant_login_amazon')
          }
        } else {
          commit('otpMessage', 'messages.server_error')
        }
      })
  },
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
