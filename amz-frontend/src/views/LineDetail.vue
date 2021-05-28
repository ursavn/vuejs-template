<template>
  <div>
    <material-card
      color="primary"
      icon="mdi-apps"
    >
      <template #title>
        {{ $t('line.title') }}
      </template>
      <v-container
        id="user-profile-view"
        tag="section"
      >
        <v-row justify="center">
          <v-col cols="12">
            <v-form>
              <v-container class="py-0">
                <div v-if="line">
                  <v-row>
                    <v-col class="text-center">
                      <h1 class="green--text">
                        {{ $t('line.connected') }}
                      </h1>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="orange--text"
                      cols="3"
                    >
                      {{ $t('line.expire_time') }}
                    </v-col>
                    <v-col cols="5">
                      <span>{{ expiredTime }}</span>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="orange--text"
                      cols="3"
                    >
                      {{ $t('line.target_type') }}
                    </v-col>
                    <v-col cols="5">
                      <span class="green--text">{{
                        line.targetType === 'GROUP'
                          ? $t('messages.group')
                          : $t('messages.user')
                      }}</span>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col
                      class="orange--text"
                      cols="3"
                    >
                      {{ $t('line.target') }}
                    </v-col>
                    <v-col cols="5">
                      <span>{{ line.target }}</span>
                    </v-col>
                  </v-row>
                </div>
                <div v-else>
                  <v-row>
                    <v-col class="text-center">
                      <h1 class="red--text">
                        {{ $t('line.not_connected') }}
                      </h1>
                    </v-col>
                  </v-row>
                  <v-row class="text-center">
                    <v-col />
                  </v-row>
                </div>

                <v-row>
                  <v-col class="text-center">
                    <v-btn
                      shaped
                      dark
                      color="green"
                      @click="linkToLine"
                    >
                      {{
                        !line.id
                          ? $t('messages.link_to_line')
                          : $t('messages.re_link_to_line')
                      }}
                    </v-btn>
                  </v-col>
                </v-row>
              </v-container>

              <v-container class="py-0" />
            </v-form>
          </v-col>
        </v-row>
      </v-container>
    </material-card>
    <v-snackbar v-model="snackbar">
      {{ message }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="snackbar = false"
        >
          {{ $t('messages.close') }}
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script>
  import moment from 'moment'
  import { get } from 'vuex-pathify'
  import { api } from '@/config/index'

  export default {
    name: 'UserProfile',
    data: () => {
      return {
        snackbar: false,
        connected: false,
        loading: false,
      }
    },
    computed: {
      ...get('user', ['user_data', 'line', 'message', 'status']),
      expiredTime () {
        return moment(this.line.createdDate)
          .add(this.line.expired)
          .format(this.$t('date_format.ja'))
      },
      expired () {
        return moment(this.line.createdDate)
          .add(this.line.expired)
          .isAfter(moment())
      },
    },
    watch: {
      settings (data) {
        if (data) {
          const expired = moment(data.expired, 'YYYY-MM-DD HH:MM:SS').isBefore(
            new Date(),
          )
          this.connected = !expired
        }
      },
      message (value) {
        this.snackbar = true
        setTimeout(() => {
          this.$store.dispatch('user/clearMessage')
          this.snackbar = false
        }, 3000)
      },
      status (value) {
        this.loading = value === 'loading'
      },
      line (value) {
        if (value.id) {
          this.connected = true
        }
      },
    },
    mounted () {
      this.$store.dispatch('user/getLine')
    },
    methods: {
      millSecToHour (value) {},
      goToChangePass () {
        this.$router.push({ name: 'UserChangePass' })
      },
      popitup (url) {
        console.log(screen.width)
        const newwindow = window.open(
          url,
          'name',
          'height=500,width=500,top=0, left=' + screen.width / 3,
        )
        if (window.focus) {
          newwindow.focus()
        }
        return false
      },
      linkToLine () {
        const state = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(
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
          redirect_uri: api.baseHost + '/callback',
          state: state,
        }
        window.location.href =
          'https://notify-bot.line.me/oauth/authorize?' +
          new URLSearchParams(param).toString()
      },
    },
  }
</script>
