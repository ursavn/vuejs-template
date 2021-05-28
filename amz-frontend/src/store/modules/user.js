import Vue from 'vue'
// Utilities
import { make } from 'vuex-pathify'

// Globals
import { IN_BROWSER } from '@/util/globals'

import { api } from '@/config/index'
import router from '@/router/index'
// import axios from 'axios'

function BadInputException (message) {
  const error = new Error(message)
  error.code = 400
  return error
}

BadInputException.prototype = Object.create(Error.prototype)
const state = {
  userList: [],
  user_data: null,
  editItem: null,
  profile: {},
  token: null,
  dark: false,
  line: {},
  drawer: {
    image: 0,
    gradient: 0,
    mini: false,
  },
  gradients: [
    'rgba(0, 0, 0, .7), rgba(0, 0, 0, .7)',
    'rgba(228, 226, 226, 1), rgba(255, 255, 255, 0.7)',
    'rgba(244, 67, 54, .8), rgba(244, 67, 54, .8)',
  ],
  images: [
    'https://demos.creative-tim.com/material-dashboard-pro/assets/img/sidebar-1.jpg',
    'https://demos.creative-tim.com/material-dashboard-pro/assets/img/sidebar-2.jpg',
    'https://demos.creative-tim.com/material-dashboard-pro/assets/img/sidebar-3.jpg',
    'https://demos.creative-tim.com/material-dashboard-pro/assets/img/sidebar-4.jpg',
  ],
  notifications: [],
  settings: null,
  rtl: false,
  login: {},
  status: '',
  logged: false,
  changed: '',
  message: '', // For changing password
  code: '', // status for syncing code
  currentActive: 0,
}

const mutations = make.mutations(state)

const actions = {
  login: ({ commit }, payload) => {
    // Start login with updating status is loading
    commit('status', 'loading')
    // Reset login data
    commit('login', {})
    // Reset logged flag
    commit('logged', false)

    const fcallback = payload.callback
    delete payload.callback

    Vue.axios
      .post(api.baseUrl + '/login', JSON.stringify(payload), {
        timeout: 8000,
      })
      .then(response => {
        // End login and update status is empty (done)
        commit('status', '')
        // Update login data
        commit('login', response.data)
        // Update logged flag
        commit('logged', true)
        commit('user_data', response.data)
        commit('token', response.data.token)
        localStorage.setItem('user_data', JSON.stringify(response.data))
        fcallback('success')
      })
      .catch(error => {
        if (error.response) {
          if (error.response.status === 400) {
            commit('message', 'messages.login_failed')
          } else if (error.response.status === 500) {
            commit('message', 'messages.server_error')
          }
        } else {
          commit('message', 'messages.server_error')
        }
        commit('status', '')

        commit('logged', false)
        fcallback('fails')
      })
  },
  logout: ({ commit }) => {
    if (!IN_BROWSER) return
    commit('logged', false)
    commit('status', '')
    localStorage.setItem('user_data', '')
    router.replace('/login')
  },
  forgotPassword: ({ commit }, payload) => {
    // Start calling API for changing password \
    commit('status', 'loading')
    Vue.axios
      .post(api.baseUrl + '/forgot-password/confirm', payload)
      .then(response => {
        // End login and update status is empty (done)
        commit('status', '')
        commit('message', 'messages.updated_successfully')
      })
      .catch(error => {
        commit('status', '')
        // Update status is error
        if (error.response) {
          console.log(error.response.data.message)
        } else {
          commit('message', 'messages.server_error')
        }
        commit('message', 'messages.server_error')
        // router.push({ name: 'Dashboard' })
      })
  },
  getUserList: ({ commit }, payload) => {
    commit('status', 'loading')
    let fullUrl = api.baseUrl + '/users'
    if (payload) {
      const query = new URLSearchParams(payload).toString()
      fullUrl += '?' + query
    }
    Vue.axios
      .get(fullUrl)
      .then(response => {
        commit('userList', response.data)
        if (response.data.totalItems === 0) {
          commit('message', 'messages.list_empty')
        }
        commit('status', '')
      })
      .catch(() => {
        commit('status', '')
        commit('message', 'messages.server_error')
      })
  },
  update: () => {},
  changePass: ({ commit }, payload) => {
    // Start login with updating status is loading
    commit('status', 'loading')

    Vue.axios
      .post(api.baseUrl + '/change-password', payload)
      .then(response => {
        commit('status', '')
        if (response.data.message === 'Old password not true!') {
          throw new BadInputException('messages.old_password_invalid')
        }
        commit('message', 'messages.updated_successfully')
      })
      .catch(() => {
        commit('status', '')

        commit('message', 'messages.server_error')
      })
  },
  clearMessage: ({ commit }) => {
    commit('message', '')
  },
  setMessage: ({ commit }, payload) => {
    commit('message', payload.message)
  },
  saveLineCode: ({ commit }, payload) => {
    commit('code', 'syncing')
    Vue.axios
      .post(api.baseUrl + '/line', payload)
      .then(response => {
        // End login and update status is empty (done)
        commit('code', '')

        commit('token', response.data.token)
        setTimeout(() => {
          router.push({ name: 'LineDetail' })
        }, 1500)
      })
      .catch(error => {
        // Update status is error
        commit('code', error.message)
      })
  },
  getLine: ({ commit }) => {
    commit('status', 'loading')
    Vue.axios
      .get(api.baseUrl + '/line')
      .then(response => {
        // End login and update status is empty (done)
        commit('status', '')
        commit('line', response.data)
      })
      .catch(error => {
        commit('status', '')
        // Update status is error
        commit('code', error.message)
      })
  },

  delete: ({ commit, dispatch }, payload) => {
    commit('status', 'loading')
    const options = payload.options
    Vue.axios
      .delete(api.baseUrl + '/users/' + payload.id)
      .then(response => {
        commit('status', '')
        dispatch('getUserList', options)
        commit('message', 'messages.user_deleted_successfully')
      })
      .catch(() => {
        commit('status', '')
        // Update status is error
        commit('message', 'messages.server_error')
      })
  },
  getLineAccessToken: ({ commit }, payload) => {
    commit('status', 'loading')

    payload.grantType = 'authorization_code'
    payload.redirectUri = api.line_callback
    payload.clientId = api.line_client_id
    payload.clientSecret = api.line_client_secret
    Vue.axios
      .post(api.baseUrl + '/line ', payload)
      .then(response => {
        // End login and update status is empty (done)
        commit('status', '')
        commit('message', 'messages.success')
      })
      .catch(error => {
        commit('status', '')
        // Update status is error
        if (error.response) {
          console.log(error.response.data.message)
        } else {
          commit('message', 'messages.server_error')
        }
        commit('message', 'messages.server_error')
        router.push('/error')
      })
  },
  AuthLINE: ({ commit }) => {
    commit('status', 'loading')
    const sate = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(
      /[xy]/g,
      function (c) {
        var r = (Math.random() * 16) | 0
        var v = c === 'x' ? r : (r & 0x3) | 0x8
        return v.toString(16)
      },
    )
    localStorage.setItem('stateLINE', state)
    const param = {
      response_type: 'code',
      scope: 'notify',
      client_id: api.line_client_id,
      redirect_uri: api.line_callback,
      state: sate,
    }

    Vue.axios
      .get(
        'https://notify-bot.line.me/oauth/authorize?' +
          new URLSearchParams(param).toString(),
      )
      .then(response => {
        commit('status', '')
        commit('profile', response.data)
        // commit('settings', settings.data)
        // console.log(settings)
      })
      .catch(error => {
        commit('status', '')
        if (error.response.data.message) {
          console.log(error.response.data.message)
        }
        commit('message', 'messages.server_error')
      })
  },
  profile: ({ commit }) => {
    commit('status', 'loading')

    Vue.axios
      .get(api.baseUrl + '/profile')
      .then(response => {
        commit('status', '')
        commit('profile', response.data)
        // commit('settings', settings.data)
        // console.log(settings)
      })
      .catch(error => {
        commit('status', '')
        if (error.response.data.message) {
          console.log(error.response.data.message)
        }
        commit('message', 'messages.server_error')
      })
  },
  sendForgotEmail: ({ commit }, payload) => {
    commit('status', 'loading')
    Vue.axios
      .post(api.baseUrl + '/forgot-password/token', payload)
      .then(response => {
        commit('status', '')
        commit('message', 'messages.successful')
      })
      .catch(error => {
        commit('status', '')
        if (error.response) {
          if (error.response.status === 400) {
            commit('message', 'messages.email_notfound')
          }
          console.log(error.response.data.message)
        }
        commit('message', 'messages.server_error')
      })
  },
  create: ({ commit }, payload) => {
    commit('status', 'loading')
    Vue.axios
      .post(api.baseUrl + '/users', payload)
      .then(response => {
        commit('status', '')
        commit('message', 'messages.save_successfully')
        setTimeout(() => {
          router.push({ name: 'UserList' })
        }, 1500)
      })
      .catch(error => {
        // Update status is error
        commit('status', '')
        if (error.response.data.message === 'Error: Email is already taken!') {
          commit('message', 'messages.email_taken')
          return
        }
        commit('message', 'messages.server_error')
      })
  },
  edit: ({ commit }, payload) => {
    commit('status', 'loading')
    Vue.axios
      .post(api.baseUrl + '/users/' + payload.id, payload)
      .then(response => {
        commit('status', '')
        commit('message', 'messages.updated_successfully')
        setTimeout(() => {
          router.push({ name: 'UserList' })
        }, 1500)
      })
      .catch(() => {
        commit('message', 'messages.server_error')
        // Update status is error
        commit('status', '')
      })
  },
  get: ({ commit }, payload) => {
    commit('status', 'loading')
    Vue.axios
      .get(api.baseUrl + '/users/' + payload.id)
      .then(response => {
        commit('status', '')
        commit('editItem', response.data)
      })
      .catch(() => {
        commit('message', 'messages.server_error')
        // Update status is error
        commit('status', '')
      })
  },
  getCurrentActive: ({ commit }) => {
    commit('status', 'loading')
    const fullUrl = api.baseUrl + '/users'
    Vue.axios
      .get(fullUrl + '?size=100')
      .then(response => {
        commit('currentActive', response.data.data.length)
        commit('message', 'error_500')
        commit('status', '')
      })
      .catch(error => {
        commit('status', '')
        commit('message', error.message)
      })
  },
}

const getters = {
  dark: (state, getters) => {
    return state.dark || getters.gradient.indexOf('255, 255, 255') === -1
  },
  gradient: state => {
    return state.gradients[state.drawer.gradient]
  },
  image: state => {
    return state.drawer.image === ''
      ? state.drawer.image
      : state.images[state.drawer.image]
  },
  status: state => {
    return state.status
  },
  code: state => {
    return state.code
  },
  token: state => {
    return state.token
  },
  user: state => {
    return state.user_data
  },
  settings: state => {
    return state.settings
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
}
