<template>
  <v-container
    id="dashboard-view"
    class="max-width"
    tag="section"
  >
    <v-row justify="center">
      <v-col
        cols="12"
        md="6"
      >
        <v-text-field
          v-if="loading"
          color="success"
          loading
          disabled
        />
        <material-card
          color="primary"
          icon="mdi-lock-outline"
        >
          <template #title>
            {{ $t('messages.change_password') }}
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
                      rules="required|min:6"
                    >
                      <v-text-field
                        v-model="oldPassword"
                        color="purple"
                        type="password"
                        prepend-icon="mdi-lock"
                        :label="$t('messages.old_password')"
                        :error="hasErrors(errors)"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                  <v-col cols="12">
                    <validation-provider
                      v-slot="{ errors }"
                      name="confirm"
                      rules="required|min:6"
                    >
                      <v-text-field
                        v-model="newPassword"
                        color="purple"
                        type="password"
                        prepend-icon="mdi-lock-outline"
                        :label="$t('messages.new_password')"
                        :error="hasErrors(errors)"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                  <v-col cols="12">
                    <validation-provider
                      v-slot="{ errors }"
                      rules="required|min:6|password:@confirm"
                    >
                      <v-text-field
                        v-model="confirmPassword"
                        color="purple"
                        type="password"
                        prepend-icon="mdi-lock-outline"
                        :label="$t('messages.confirm_password')"
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
                  {{ $t('messages.save') }}
                </v-btn>
              </v-card-actions>
            </v-form>
          </validation-observer>
        </material-card>
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
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
  import {
    ValidationProvider,
    ValidationObserver,
    extend,
  } from 'vee-validate'
  import { required, min } from 'vee-validate/dist/rules'
  import { get } from 'vuex-pathify'

  export default {
    components: {
      ValidationProvider,
      ValidationObserver,
    },
    data: () => {
      return {
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
        loading: false,
        snackbar: false,
        result: '',
      }
    },
    computed: {
      ...get('user', ['status', 'message']),
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
      },
      message (value) {
        this.snackbar = true
        this.result = this.$t(value)
        setTimeout(() => {
          this.$store.dispatch('user/clearMessage')
          this.snackbar = false
        }, 3000)
      },
    },
    created () {
      this.$options.name = this.$t('messages.change_password')
      extend('required', {
        ...required,
        message: this.$t('validator.required'),
      })

      extend('min', {
        ...min,
        message: this.$t('validator.password_invalid'),
      })
      extend('password', {
        params: ['target'],
        validate (value, { target }) {
          return value === target
        },
        message: this.$t('validator.confirm_password_invalid'),
      })
    },
    methods: {
      onSubmit () {
        const payload = {
          oldPassword: this.oldPassword,
          newPassword: this.newPassword,
        }
        this.$store.dispatch('user/changePass', payload)
        this.newPassword = ''
        this.confirmPassword = ''
      },
      hasErrors (errors) {
        return errors.length > 0
      },
    },
  }
</script>
