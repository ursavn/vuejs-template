<template>
  <v-container
    id="user-profile-view"
    fluid
    tag="section"
  >
    <v-row justify="center">
      <v-col
        cols="11"
        md="7"
      >
        <material-card
          color="primary"
          :loading="loading"
          icon="mdi-account-outline"
        >
          <template #title>
            {{ $t('messages.add_new_user_title') }}
          </template>
          <validation-observer v-slot="{ handleSubmit }">
            <v-form
              lazy-validation
              @submit.prevent="handleSubmit(onSubmit)"
            >
              <v-container class="pl-6 py-0">
                <!-- Email input-->
                <v-row>
                  <v-col
                    cols="12"
                    md="6"
                  >
                    <validation-provider
                      v-slot="{ errors }"
                      rules="required|email"
                    >
                      <v-text-field
                        v-model="user.email"
                        color="purple"
                        :label="$t('messages.email')"
                        :error="hasErrors(errors)"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                </v-row>
                <!-- Full name input -->
                <v-row>
                  <v-col
                    cols="12"
                    md="6"
                  >
                    <validation-provider
                      v-slot="{ errors }"
                      rules="required"
                    >
                      <v-text-field
                        v-model="user.fullName"
                        color="purple"
                        :label="$t('messages.full_name')"
                        :error="hasErrors(errors)"
                      /><span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                </v-row>
                <!-- Password input -->
                <v-row>
                  <v-col
                    cols="12"
                    md="6"
                  >
                    <validation-provider
                      v-slot="{ errors }"
                      name="confirm"
                      rules="required|min:6"
                    >
                      <v-text-field
                        v-model="user.password"
                        color="purple"
                        :label="$t('messages.password')"
                        type="password"
                        :error="hasErrors(errors)"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                </v-row>
                <!-- re Password input -->
                <v-row>
                  <v-col
                    cols="12"
                    md="6"
                  >
                    <validation-provider
                      v-slot="{ errors }"
                      rules="required|min:6|password:@confirm"
                    >
                      <v-text-field
                        v-model="repassword"
                        color="purple"
                        :label="$t('messages.confirm_password')"
                        type="password"
                        :error="hasErrors(errors)"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                </v-row>

                <v-row>
                  <v-col
                    cols="5"
                    offset="8"
                  >
                    <div class="flex">
                      <v-btn
                        text
                        color="error"
                        outlined
                        class="mr-4"
                        :disabled="loading"
                        @click="$router.push('/user-list')"
                      >
                        {{ $t('messages.cancel') }}
                      </v-btn>
                      <v-btn
                        text
                        color="primary"
                        outlined
                        type="submit"
                        class="mr-4"
                        :disabled="loading"
                      >
                        {{ $t('messages.save') }}
                      </v-btn>
                    </div>
                  </v-col>
                </v-row>
              </v-container>
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
  import { ValidationProvider, ValidationObserver, extend } from 'vee-validate'
  import { required, email, min } from 'vee-validate/dist/rules'
  import { get } from 'vuex-pathify'

  export default {
    name: 'AddUser',
    components: {
      ValidationProvider,
      ValidationObserver,
    },
    data () {
      return {
        user: {
          fullName: '',
          password: '',
          email: '',
        },
        result: '',
        snackbar: false,
        repassword: '',
        loading: false,
        errors: [],
      }
    },
    computed: {
      ...get('user', ['status', 'message']),
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
        this.snackbar = value === ''
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
      extend('required', {
        ...required,
        message: this.$t('validator.required'),
      })

      extend('min', {
        ...min,
        message: this.$t('validator.password_invalid'),
      })
      extend('email', {
        ...email,
        message: this.$t('validator.email_invalid'),
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
      hasErrors (errors) {
        return errors.length > 0
      },
      onSubmit () {
        this.$store.dispatch('user/create', this.user)
      },
    },
  }
</script>
