import Vue from 'vue'
import { api } from '@/config/index'

// Pathify
import { make } from 'vuex-pathify'

const state = {
  automation: '',
  users: '',
  amazonAccount: '',
  message: '',
  status: '',
}

const mutations = make.mutations(state)

const actions = {
  init: ({ commit }) => {
    commit('status', 'loading')

    try {
      const requestAutomation = Vue.axios.get(api.baseUrl + '/automations')
      const requestUsers = Vue.axios.get(api.baseUrl + '/users')
      const requestAmazonAccount = Vue.axios.get(api.baseUrl + '/amazon')
      Vue.axios
        .all([requestAutomation, requestUsers, requestAmazonAccount])
        .then(
          Vue.axios.spread((...responses) => {
            const responseAutomation = responses[0]
            const responseUsers = responses[1]
            const responesAmazonAccount = responses[2]

            const currentOnlineAutomation = responseAutomation.data.data.filter(
              item => {
                return item.status === true
              },
            ).length
            const currentActiveUser = responseUsers.data.data.length
            commit('automation', currentOnlineAutomation)
            commit('users', currentActiveUser)
            commit('amazonAccount', responesAmazonAccount.data.totalItems)
          }),
        )
    } catch (error) {
      commit('message', 'messages.server_error')
    } finally {
      commit('status', '')
    }
  },
  clearMessage ({ commit }) {
    commit('message', '')
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
