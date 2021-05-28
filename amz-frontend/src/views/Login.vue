<template>
  <v-img
    dark
    gradient="to top, rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)"
    min-height="100vh"
    max-height="100vh"
    src="https://vuetify-material-dashboard.vuetifyjs.com/img/login.d6d3bb09.jpg"
    lazy-src="https://vuetify-material-dashboard.vuetifyjs.com/img/login.d6d3bb09.jpg"
  >
    <validation-observer v-slot="{ handleSubmit }">
      <v-form
        ref="form"
        lazy-validation
        @submit.prevent="handleSubmit(onSubmit)"
      >
        <v-card
          class="mt-12 mx-auto"
          max-width="350"
          light
          rounded
          :loading="loading"
          style="transform-origin: center top 0;"
        >
          <v-card-title class="align-start">
            <v-sheet
              elevation="6"
              rounded
              light
              class="transition-swing accent overflow-hidden"
              style="max-width: 100%; width: 100%"
            >
              <div class="text-center pa-5">
                <div class="text-h4 font-weight-bold white--text">
                  {{ $t('messages.login') }}
                </div>
              </div>
            </v-sheet>
          </v-card-title>

          <v-card-text>
            <validation-provider
              v-slot="{ errors }"
              rules="required|email"
            >
              <v-text-field
                v-model="email"
                :counter="256"
                :disabled="loading"
                :clearable="!loading"
                :error="hasErrors(errors)"
                :label="$t('messages.email')"
                prepend-icon="mdi-email-outline"
                solo
              />
              <span class="error--text">{{ errors[0] }}</span>
            </validation-provider>

            <validation-provider
              v-slot="{ errors }"
              rules="required|min:6"
            >
              <v-text-field
                v-model="password"
                :disabled="loading"
                :error="hasErrors(errors)"
                :label="$t('messages.password')"
                :hint="$t('messages.password_hint')"
                type="password"
                prepend-icon="mdi-lock-outline"
                solo
              />
              <span class="error--text">{{ errors[0] }}</span>
            </validation-provider>
            <div class="text-right">
              <router-link
                to="/forgot-pass"
                class="text--primary"
              >
                {{ $t('messages.go_to_forgot_password') }}
              </router-link>
            </div>
          </v-card-text>

          <v-card-actions class="d-flex align-center justify-center">
            <v-btn
              rounded
              dark
              large
              color="pink"
              type="submit"
              class="mr-4"
              :disabled="loading"
            >
              {{ $t('messages.sign_in') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-form>
    </validation-observer>
    <v-snackbar v-model="snackbar">
      {{ parsedMessage }}

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
  </v-img>
</template>

<script>
  import { ValidationProvider, ValidationObserver, extend } from 'vee-validate'
  import { required, email, min } from 'vee-validate/dist/rules'
  import { get } from 'vuex-pathify'

  export default {
    name: 'Login',
    components: {
      ValidationProvider,
      ValidationObserver,
    },
    data: () => {
      return {
        snackbar: false,
        password: '',
        email: '',
        errors: [],
        loading: false,
        parsedMessage: '',
      }
    },
    computed: {
      ...get('user', ['message', 'dark', 'gradient', 'image', 'status']),
    },
    watch: {
      message (val) {
        this.snackbar = true
        this.parsedMessage = this.$t(val)
        setTimeout(() => {
          this.snackbar = false
          this.$store.dispatch('user/clearMessage')
        }, 3000)
      },
      status (value) {
        this.loading = value === 'loading'
      },
    },
    mounted () {
      // Define the rule globally
      extend('required', {
        ...required,
        message: this.$t('validator.required'),
      })

      extend('email', {
        ...email,
        message: this.$t('validator.email_invalid'),
      })

      extend('min', {
        ...min,
        message: this.$t('validator.password_invalid'),
      })
    },
    methods: {
      // Validator function
      onSubmit () {
        const payload = {
          email: this.email,
          password: this.password,
          callback: this.handleLoginResult.bind(this),
        }
        this.$store.dispatch('user/login', payload)
      },
      handleLoginResult (result) {
        const query = this.$route.query
        switch (result) {
          case 'success':
            if (Object.keys(query).length > 0) {
              this.$router.replace(query.redirect)
            } else {
              this.$router.replace({ name: 'Dashboard' })
            }
            break
          case 'failure':
          default:
            break
        }
      },
      hasErrors (errors) {
        return errors.length > 0
      },
    },
  }
</script>

<style scoped></style>
