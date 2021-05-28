<template>
  <v-fade-transition mode="out-in">
    <router-view />
  </v-fade-transition>
</template>

<script>
// Styles
  import '@/styles/overrides.sass'

  export default {
    name: 'App',
    metaInfo: {
      title: 'App',
      titleTemplate: 'Amazon Tool',
      htmlAttrs: { lang: 'ja' },
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      ],
    },
    mounted () {
      window.liff.init({
        liffId: '1655775896-yeWvo1L3',
      })
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')
      if (user) {
        this.$store.commit('user/logged', true)
        this.$store.commit('user/user', user)
      }
      if (token) {
        this.$tore.commit('user/token', token)
      }
    // Check token needs API
    },
  }
</script>
