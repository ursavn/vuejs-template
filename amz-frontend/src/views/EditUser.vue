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
            {{ $t('user.edit_user.title') }}
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
                      v-slot="{ emailErrors }"
                      rules="required|email"
                    >
                      <v-text-field
                        v-model="user.email"
                        color="purple"
                        :label="$t('messages.email')"
                        :error="hasErrors(emailErrors)"
                      />
                      <span class="error--text">{{ emailErrors[0] }}</span>
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
                      v-slot="{ fullNameErrors }"
                      rules="required"
                    >
                      <v-text-field
                        v-model="user.fullName"
                        color="purple"
                        :label="$t('messages.full_name')"
                        :error="hasErrors(fullNameErrors)"
                      /><span class="error--text">{{ fullNameErrors[0] }}</span>
                    </validation-provider>
                  </v-col>
                </v-row>

                <v-row>
                  <v-col cols="4">
                    <div>
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
                        large
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
        user: {},
        snackbar: false,
        repassword: '',
        loading: false,
        errors: [],
        result: '',
      }
    },
    computed: {
      ...get('user', ['editItem', 'status', 'message']),
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
        this.snackbar = value === '' && this.message !== ''
      },
      editItem (value) {
        this.user = value
      },
      message (value) {
        this.snackbar = true
        this.result = this.$t(value)
        setTimeout(() => {
          this.snackbar = false
          this.$store.dispatch('user/clearMessage')
        }, 3000)
      },
    },
    created () {
      this.$store.dispatch('user/get', {
        id: this.$route.params.id,
      })
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
        this.$store.dispatch('user/edit', this.user)
      },
    },
  }
</script>
