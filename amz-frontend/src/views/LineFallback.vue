<template>
  <div>
    <v-card
      :loading="loading"
      class="mx-auto"
      max-width="344"
    >
      <v-card-title>
        <h4>
          {{ $t('messages.connected_to_line') }}
        </h4>
      </v-card-title>

      <v-card-subtitle class="mt-1 text-center">
        <h1 class="green--text">
          {{ $t('messages.connected_successful') }}
        </h1>
      </v-card-subtitle>

      <v-card-actions class="d-flex justify-center">
        <v-btn
          color="orange lighten-2"
          text
          :disabled="!success"
          @click.stop="goToProfile"
        >
          {{ $t('messages.connected_successful') }}
        </v-btn>
      </v-card-actions>
    </v-card>
    <v-dialog
      v-model="loading"
      max-width="500px"
    >
      <v-card>
        <v-card-title>
          <span v-html="$t('messages.linking_your_line_account')" />
        </v-card-title>
        <v-card-text>
          <div class="text-center">
            <v-progress-circular
              :size="70"
              :width="7"
              color="primarys"
              indeterminate
            />
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
  import { get } from 'vuex-pathify'
  export default {
    name: 'LineFallback',
    data () {
      return {
        loading: false,
        success: false,
      }
    },
    computed: {
      ...get('user', ['message', 'status']),
    },
    watch: {
      message (val) {
        this.success = val === 'messages.success'
      },
      status (val) {
        this.loading = val === 'loading'
      },
    },
    created () {
      const code = this.$route.query.code
      const state = this.$route.query.state

      if (state !== localStorage.getItem('stateLINE')) {
        console.log(state + 'and' + localStorage.getItem('stateLINE'))
        this.$router.push('/error')
      } else {
        if (
          this.$route.query.error === 'access_denied' &&
          this.$route.query.error_description === 'user'
        ) {
          // https://testamazon.ursa.vn/callback?error=access_denied&error_description=user%20canceled&state=cd047115-7a17-40e6-83ca-2879e8f9d36c
          this.$router.push('/line')
          return
        }
        this.$store.dispatch('user/getLineAccessToken', { code: code })
      }
    },
    mounted () {},
    methods: {
      goToProfile () {
        this.$router.replace({ name: 'User Profile' })
      },
    },
  }
</script>

<style lang="scss">
.loading {
  animation: roll 3s infinite;
  transform: rotate(30deg);
}
@keyframes roll {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
