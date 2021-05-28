<template>
  <v-container
    id="user-profile-view"
    fluid
    tag="section"
  >
    <v-row justify="center">
      <v-col
        cols="12"
        md="8"
      >
        <material-card
          color="primary"
          icon="mdi-account-outline"
        >
          <template #title>
            {{ $t('messages.profile') }}
          </template>

          <v-form>
            <v-container class="py-0">
              <v-row>
                <v-col
                  cols="12"
                  md="6"
                >
                  <v-text-field
                    v-model="profile.email"
                    disabled
                    color="purple"
                    :label="$t('messages.email')"
                    prepend-inner-icon="mdi-email-outline"
                  />
                </v-col>

                <v-col
                  cols="12"
                  md="6"
                >
                  <v-text-field
                    v-model="profile.fullName"
                    disabled
                    color="purple"
                    :label="$t('messages.full_name')"
                    prepend-inner-icon="mdi-account-outline"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col
                  md="4"
                  cols="4"
                >
                  <v-checkbox
                    readonly
                    :label="$t('messages.status')"
                    :value="profile.status"
                    :color="profile.status === 'ACTIVE' ? 'green' : 'red'"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="6">
                  <span>{{ $t('messages.created_date') }}:
                    {{ profile.createdDate | formatDate }}</span>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="6">
                  <span>{{ $t('messages.last_modified_date') }}:
                    {{ profile.modifiedDate | formatDate }}</span>
                </v-col>
              </v-row>
              <v-row>
                <v-col
                  cols="6"
                  md="6"
                >
                  <v-btn
                    shaped
                    dark
                    color="pink"
                    @click="goToChangePass"
                  >
                    {{ $t('messages.change_your_password') }}
                  </v-btn>
                </v-col>
                <v-col
                  cols="6"
                  md="6"
                >
                  <p
                    v-if="connected"
                    class="text--primary"
                  >
                    {{ $t('messages.connected_to_line') }}
                  </p>
                </v-col>
              </v-row>
            </v-container>
          </v-form>
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
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
  import moment from 'moment'
  import { get } from 'vuex-pathify'

  export default {
    name: 'UserProfile',
    data: () => {
      return {
        snackbar: false,
        connected: false,
      }
    },
    computed: {
      ...get('user', ['code', 'token', 'settings', 'profile', 'message']),
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
      profile (value) {
        console.log(value)
      },
    },
    mounted () {
      this.$store.dispatch('user/profile')
    },
    methods: {
      goToChangePass () {
        this.$router.push({ name: 'UserChangePass' })
      },
      linkToLine () {
        if (!window.liff.isLoggedIn()) {
          window.liff.login({
            redirectUri: 'https://testamazon.ursa.vn/callback/',
          })
        }
      },
    },
  }
</script>
