<template>
  <v-img
    light
    gradient="to top, rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)"
    min-height="100vh"
    max-height="100vh"
    src="https://vuetify-material-dashboard.vuetifyjs.com/img/login.d6d3bb09.jpg"
    lazy-src="https://vuetify-material-dashboard.vuetifyjs.com/img/login.d6d3bb09.jpg"
  >
    <v-container
      id="forgot-password-view"
      fluid
      tag="section"
    >
      <v-row justify="center">
        <v-col
          cols="12"
          md="6"
        >
          <material-card
            :loading="loading"
            color="primary"
            icon="mdi-lock-outline"
          >
            <template #title>
              {{ $t('messages.forgot_password') }}
            </template>

            <validation-observer v-slot="{ handleSubmit }">
              <v-form
                lazy-validation
                @submit.prevent="handleSubmit(onSubmit)"
              >
                <v-container class="py-0">
                  <v-row>
                    <v-col cols="12">
                      <validation-provider
                        v-slot="{ errors }"
                        rules="required|email"
                      >
                        <v-text-field
                          v-model="email"
                          color="purple"
                          prepend-icon="mdi-email-outline"
                          :label="$t('messages.email')"
                          :error="hasErrors(errors)"
                        />
                        <span class="error--text">{{ errors[0] }}</span>
                      </validation-provider>
                    </v-col>
                  </v-row>
                </v-container>
                <v-card-actions class="d-flex align-center justify-center">
                  <v-btn
                    dark
                    large
                    color="pink"
                    type="submit"
                    class="mr-4"
                  >
                    {{ $t('messages.send') }}
                  </v-btn>
                </v-card-actions>
              </v-form>
            </validation-observer>
          </material-card>
        </v-col>
      </v-row>
      <v-snackbar v-model="snackbar">
        {{ result }}

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
    </v-container>
  </v-img>
</template>

<script>
  import { ValidationProvider, ValidationObserver, extend } from 'vee-validate'
  import { required, email } from 'vee-validate/dist/rules'
  import { get } from 'vuex-pathify'
  export default {
    name: 'UserForgotPassword',
    components: {
      ValidationProvider,
      ValidationObserver,
    },
    data: () => {
      return {
        loading: false,
        snackbar: false,
        email: '',
        result: '',
      }
    },
    computed: {
      ...get('user', ['message', 'status']),
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
      },
      message (value) {
        this.result = this.$t(value)
        this.snackbar = true
        setTimeout(() => {
          if (value === 'messages.successful') {
            this.$router.push({ name: 'Login' })
          }
          this.snackbar = false
          this.$store.dispatch('user/clearMessage')
        }, 3000)
      },
    },
    created () {
      extend('required', {
        ...required,
        message: this.$t('validator.required'),
      })
      extend('email', {
        ...email,
        message: this.$t('validator.email_invalid'),
      })
    },
    methods: {
      onSubmit () {
        const payload = {
          email: this.email,
        }
        this.$store.dispatch('user/sendForgotEmail', payload)
      },
      hasErrors (errors) {
        return errors.length > 0
      },
    },
  }
</script>

<style scoped></style>
