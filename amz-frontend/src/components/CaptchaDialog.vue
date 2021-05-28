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
            {{ $t('messages.please_enter_captcha') }}
          </v-card-title>
          <v-card-text class="text-center mt-5">
            <v-row>
              <v-col><v-img :src="data.message" /></v-col>
            </v-row>
            <v-row>
              <v-col>
                <v-text-field
                  v-model="captchaString"
                  :label="$t('messages.captcha')"
                  :error="captchaError !== ''"
                />
                <span class="float-left red--text">{{ captchaError }}</span>
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
      after: Boolean,
    },
    data () {
      return {
        result: false,
        loading: false,
        captchaString: '',
        captchaError: '',
        editDialog: false,
      }
    },

    computed: {
      ...get('account', ['captchaStatus', 'captchaMessage', 'captchaResponse']),
    },
    watch: {
      captchaStatus (value) {
        this.loading = value === 'loading'
      },
      captchaMessage (value) {
        if (value === 'messages.save_successfully') {
          this.$emit('on-show-snack-bar', 'messages.save_successfully', 5000)

          this.result = true
          this.close()
        } else {
          this.result = false
          this.$emit('on-show-snack-bar', 'messages.captcha_failed', 5000)
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
        this.$emit('close', this.result, this.captchaResponse)
      },
      onSubmit () {
        this.result = false
        this.captchaError = ''
        if (this.captchaString !== '') {
          this.$store.dispatch('account/sendCaptcha', {
            email: this.data.email,
            code: this.captchaString,
          })
        } else {
          this.captchaError = this.$t('validator.required')
        }
      },
    },
  }
</script>

<style></style>
