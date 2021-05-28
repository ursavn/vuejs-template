<template>
  <v-dialog
    v-model="show"
    persistent
    max-width="400"
  >
    <v-container>
      <v-form
        ref="form"
        lazy-validation
        @submit.prevent="onSubmit"
      >
        <v-card>
          <v-card-title>
            {{ $t('messages.please_check_otp') }}
          </v-card-title>
          <v-card-text class="text-center mt-5">
            <v-row>
              <v-col>
                <v-text-field
                  v-model="otpString"
                  :label="$t('messages.otp')"
                  :error="otpError !== ''"
                />
                <span class="float-left red--text">{{ otpError }}</span>
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions>
            <v-spacer />
            <v-btn
              color="red darken-1"
              text
              :disabled="loading"
              @click="close()"
            >
              {{ $t('messages.cancel') }}
            </v-btn>
            <v-spacer />
            <v-btn
              color="red darken-1"
              text
              :disabled="loading"
              type="submit"
            >
              {{ $t('messages.save') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-form>
    </v-container>
  </v-dialog>
</template>

<script>
  import { get } from 'vuex-pathify'
  export default {
    name: 'CaptchaDialog',
    props: {
      data: Object,
      show: Boolean,
    },
    data () {
      return {
        loading: false,
        otpString: '',
        otpError: '',
        editDialog: false,
      }
    },

    computed: {
      ...get('account', ['otpStatus', 'otpMessage']),
    },
    watch: {
      otpStatus (value) {
        this.loading = value === 'loading'
      },
      otpMessage (value) {
        if (value === 'messages.save_successfully') {
          this.$emit('on-show-snack-bar', 'messages.save_successfully', 5000)
          this.close()
        } else {
          this.$emit('on-show-snack-bar', 'messages.otp_failed', 5000)
        }
      },
      show: {
        immediate: true,
        handler (value) {
          this.editDialog = value
        },
      },
    },
    methods: {
      close () {
        this.$emit('close-create-dialog')
        this.$emit('close')
      },
      onSubmit () {
        this.otpError = ''
        if (this.otpString !== '') {
          this.$store.dispatch('account/sendOtp', {
            email: this.data.email,
            code: this.otpString,
          })
        } else {
          this.otpError = this.$t('validator.required')
        }
      },
    },
  }
</script>

<style></style>
