<template>
  <material-card
    color="primary"
    icon="mdi-amazon"
  >
    <v-data-table
      :headers="headers"
      :items="accounts"
      :options.sync="options"
      :server-items-length="totalItems"
      class="elevation-1"
      :loading-text="$t('messages.loading_item')"
      :loading="loading"
      :footer-props="{
        itemsPerPageOptions: [5, 10, 15, 100],
        showFirstLastPage: true,
        'items-per-page-text': $t('v-table.headers.items-per-page'),
      }"
    >
      <template slot="no-data">
        {{ $t('messages.list_empty') }}
      </template>
      <template v-slot:item.createdDate="{ item }">
        {{ item.createdDate | formatDate }}
      </template>
      <template v-slot:item.status="{ item }">
        <span :class="item.status === 'ACTIVE' ? 'green--text' : 'red--text'">{{
          $t('messages.' + item.status)
        }}</span>
      </template>
      <template v-slot:item.modifiedDate="{ item }">
        {{ item.modifiedDate | formatDate }}
      </template>
      <template v-slot:footer.page-text="items">
        {{ items.pageStart }} - {{ items.pageStop }}
        {{ $t('v-table.headers.page-text') }}
        {{ items.itemsLength }}
      </template>
      <template v-slot:top>
        <v-toolbar flat>
          <v-container class="mt-5">
            <v-row>
              <v-col cols="3">
                <v-toolbar-title class="float-left">
                  {{ $t('messages.list_amazon_accounts') }}
                </v-toolbar-title>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
              </v-col>
              <v-col
                cols="4"
                md="4"
              >
                <v-text-field
                  v-model="filterName"
                  hide-details
                  prepend-icon="mdi-magnify"
                  :placeholder="$t('messages.filter_name')"
                  single-line
                  max-width="50px"
                  @keyup.enter="filter"
                  @click:prepend="filter"
                />
              </v-col>
              <v-col cols="2">
                <v-select
                  v-model="filterStatus"
                  item-text="value"
                  item-value="key"
                  :items="filterStatusItem"
                  :label="$t('messages.filter_status')"
                />
              </v-col>

              <v-col>
                <!-- create/edit dialog -->
                <v-dialog
                  v-model="create"
                  max-width="500px"
                  persistent
                  @close="onDialogClose"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                      color="primary"
                      dark
                      class="mb-2 float-right"
                      v-bind="attrs"
                      :disabled="loading"
                      v-on="on"
                    >
                      {{ $t('messages.new_account') }}
                    </v-btn>
                  </template>

                  <!-- create/edit dialog -->
                  <validation-observer v-slot="{ handleSubmit }">
                    <v-form
                      ref="form"
                      lazy-validation
                      @submit.prevent="handleSubmit(onCreate)"
                    >
                      <v-card :loading="loading">
                        <v-card-title>
                          {{ formTitle }}
                        </v-card-title>

                        <v-card-text>
                          <v-container>
                            <v-row>
                              <v-col
                                cols="12"
                                sm="12"
                                md="6"
                              >
                                <validation-provider
                                  v-slot="{ errors }"
                                  rules="required|email"
                                >
                                  <v-text-field
                                    v-model="editedItem.email"
                                    :counter="320"
                                    :error="
                                      hasErrors(errors) || errorEmail !== ''
                                    "
                                    :label="$t('messages.email')"
                                  />
                                  <span class="error--text">{{
                                    errors[0] || errorEmail
                                  }}</span>
                                </validation-provider>
                              </v-col>
                              <v-col
                                cols="12"
                                sm="12"
                                md="6"
                              >
                                <validation-provider
                                  v-if="editedIndex === -1"
                                  v-slot="{ errors }"
                                  rules="required"
                                >
                                  <v-text-field
                                    v-model="editedItem.password"
                                    :error="
                                      hasErrors(errors) || errorPassword !== ''
                                    "
                                    :label="$t('messages.password')"
                                    type="password"
                                  />
                                  <span class="error--text">{{
                                    errors[0] || errorPassword
                                  }}</span>
                                </validation-provider>
                              </v-col>
                            </v-row>
                          </v-container>
                        </v-card-text>

                        <v-card-actions>
                          <v-spacer />

                          <v-btn
                            outlined
                            color="red darken-1"
                            text
                            :disabled="loading"
                            @click="closeDialog"
                          >
                            {{ $t('messages.cancel') }}
                          </v-btn>
                          <v-btn
                            outlined
                            color="green darken-1"
                            text
                            :disabled="loading"
                            type="submit"
                          >
                            {{ $t('messages.save') }}
                          </v-btn>
                        </v-card-actions>
                      </v-card>
                    </v-form>
                  </validation-observer>
                </v-dialog>
              </v-col>
            </v-row>
          </v-container>
        </v-toolbar>
      </template>
      <template v-slot:item.actions="{ item }">
        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-icon
              small
              class="mr-2"
              v-bind="attrs"
              v-on="on"
              @click="deleteItem(item)"
            >
              mdi-delete
            </v-icon>
          </template>
          <span>{{ $t('v-table.tooltip.delete') }}</span>
        </v-tooltip>

        <v-tooltip top>
          <template v-slot:activator="{ on, attrs }">
            <v-icon
              small
              class="mr-2"
              v-bind="attrs"
              right
              :disabled="!reLoginable(item)"
              v-on="on"
              @click="reLogin(item)"
            >
              mdi-login
            </v-icon>
          </template>
          <span>{{ $t('v-table.tooltip.relogin') }}</span>
        </v-tooltip>
      </template>
      <template v-slot:no-data>
        <v-btn
          color="primary"
          @click="initialize()"
        >
          {{ $t('messages.reload') }}
        </v-btn>
      </template>
    </v-data-table>
    <!-- Dialogs -->

    <!-- polling timer -->
    <v-dialog
      v-model="pollingDialog"
      persistent
      max-width="290"
    >
      <v-card>
        <v-card-title>
          {{ $t('messages.try_to_login_to_amazon') }}
        </v-card-title>
        <v-card-text class="text-center mt-5">
          <v-progress-circular
            :rotate="-90"
            :size="100"
            :width="15"
            :value="pollingTime"
            color="primary"
          >
            {{ Math.floor(defaultPollingTime * (pollingTime / 100)) }} s
          </v-progress-circular>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="red darken-1"
            text
            @click="cancelPolling()"
          >
            {{ $t('messages.cancel') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!-- delete dialog -->
    <v-dialog
      v-model="dialog"
      persistent
      max-width="290"
    >
      <v-card>
        <v-card-title>
          {{ $t('messages.delete_account') }}
        </v-card-title>
        <v-card-text>
          <span
            v-html="
              $t('messages.do_you_want_to_delete_account', {
                account: editedItem.email,
              })
            "
          />
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="green darken-1"
            text
            @click="dialog = false"
          >
            {{ $t('messages.no') }}
          </v-btn>
          <v-btn
            color="red darken-1"
            text
            @click="processDeleteItem()"
          >
            {{ $t('messages.yes') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <!--captcha dialog dev :data="email:email message:data.message" -->
    <captcha-dialog
      :show="captchaDialog"
      :data="captchaDialogData"
      @close="closeCatchaDialog"
      @close-create-dialog="onDialogClose"
      @on-show-snack-bar="onShowSnackBar"
    />
    <otp-dialog
      :show="otpDialog"
      :data="otpDialogData"
      @close="closeOtpDialog"
      @close-create-dialog="onDialogClose"
      @on-show-snack-bar="onShowSnackBar"
    />

    <v-dialog
      v-model="changePasswordDialog"
      persistent
      max-width="290"
    >
      <v-card>
        <v-card-title>
          {{ $t('messages.please_change_amazon_password') }}
        </v-card-title>
        <v-card-actions>
          <v-spacer />
          <v-btn
            color="green darken-1"
            text
            @click="dialog = false"
          >
            {{ $t('messages.ok') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- sncakbar -->
    <v-snackbar
      v-model="snackbar"
      :timeout="snackbarTimeout"
    >
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
  </material-card>
</template>

<script>
  import { get } from 'vuex-pathify'
  import { ValidationProvider, ValidationObserver, extend } from 'vee-validate'
  import { email, required } from 'vee-validate/dist/rules'
  import captchaDialog from '../components/CaptchaDialog'
  import otpDialog from '../components/OtpDialog'
  export default {
    name: 'AccountList',
    components: {
      otpDialog: otpDialog,
      captchaDialog: captchaDialog,
      ValidationProvider,
      ValidationObserver,
    },
    data: () => {
      return {
        dialog: false,
        dialogDelete: false,
        options: {},
        accounts: [],
        headers: [],
        editedIndex: -1,
        editedItem: {
          id: '',
          email: '',
          password: '',
          createdDate: '',
          modifiedDate: '',
        },
        defaultItem: {
          id: '',
          email: '',
          createdDate: '',
          modifiedDate: '',
          accounts: '',
        },
        currentPage: 1,
        totalItems: 1,
        totalPages: 1,
        loading: false,
        formTitle: '',
        snackbar: false,
        result: '',
        create: false,
        polling: false,
        timerPolling: null,
        snackbarTimeout: 5000,
        pollingTime: 100,
        defaultPollingTime: 60, // second
        pollingDialog: false,
        filterName: '',
        filterStatus: '',
        currentOptions: {},
        filterStatusItem: [],
        verifyDialog: false,
        captchaDialog: false,
        otpDialog: false,
        changePasswordDialog: false,
        captchaDialogData: {},
        otpDialogData: {},
        errorEmail: '',
        errorPassword: '',
      }
    },
    computed: {
      ...get('account', [
        'list',
        'status',
        'message',
        'creating',
        'pollingStatus',
      ]),
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
      },
      creating (value) {
        this.onResponseStatus(value.status, value)
      },
      pollingTime (value) {
        if (value <= 0) {
          this.cancelPolling()
          this.pollingTime = 100
          this.pollingDialog = false
        // send message
        }
      },
      pollingStatus (value) {
        if (this.polling && value.status === 'ACTIVE') {
          this.cancelPolling()
          this.onShowSnackBar('messages.save_successfully')
          this.polling = false
          this.$store.dispatch('account/clearStatus')
          this.$store.dispatch('account/fetch')
        }
        if (this.polling && value.status === 'CAPTCHA') {
          this.cancelPolling()
          this.onShowSnackBar('messages.please_enter_captcha')
          this.polling = false
          const payload = {
            email: value.email,
            message: value.statusNote,
          }
          this.onResponseStatus(value.status, payload)
          this.$store.dispatch('account/clearStatus')
          this.$store.dispatch('account/fetch')
        }
      },
      message (value) {
        this.onShowSnackBar(value)
      },
      list (value) {
        this.$set(this, 'accounts', value.data)
        this.currentPage = value.currentPage
        this.totalItems = value.totalItems
        this.totalPages = value.totalPages
        this.totalPages = value.totalPages
      },
      create () {
        this.formTitle =
          this.editedIndex === -1
            ? this.$t('messages.create_new_amazon_account')
            : this.$t('messages.edit_amazon_account')
      },
      filterStatus: {
        immediate: false,
        handler () {
          this.filter()
        },
      },
      options: {
        handler () {
          this.initialize()
        },
        deep: true,
      },
    },
    mounted () {
      this.filterStatusItem = Object.entries(
        this.$t('account.status_filters'),
      ).map(([key, value]) => ({ key, value }))

      extend('required', {
        ...required,
        message: this.$t('validator.required'),
      })

      extend('email', {
        ...email,
        message: this.$t('validator.email_invalid'),
      })
      this.headers = [
        {
          text: this.$t('v-table.headers.id'),
          align: 'start',
          value: 'id',
        },
        { text: this.$t('v-table.headers.email'), value: 'email' },
        {
          name: 'status',
          text: this.$t('v-table.headers.status'),
          value: 'status',
        },
        { text: this.$t('v-table.headers.created_date'), value: 'createdDate' },
        { text: this.$t('v-table.headers.modified_date'), value: 'modifiedDate' },
        {
          text: this.$t('v-table.headers.actions'),
          value: 'actions',
          sortable: false,
        },
      ]
    },
    beforeDestroy () {
      this.cancelPolling()
    },
    methods: {
      initialize (filterName = '', filterStatus = '') {
        const tmpOptions = { ...this.options }
        tmpOptions.size = tmpOptions.itemsPerPage
        tmpOptions.sortDesc =
          tmpOptions.sortDesc.length === 0 ? true : tmpOptions.sortDesc
        tmpOptions.sortBy =
          tmpOptions.sortBy.length === 0 ? 'id' : tmpOptions.sortBy

        delete tmpOptions.groupDesc
        delete tmpOptions.groupBy
        delete tmpOptions.mustSort
        delete tmpOptions.multiSort
        if (filterName) {
          tmpOptions.search = filterName
          tmpOptions.page = 1
        }
        if (filterStatus && filterStatus !== 'All') {
          tmpOptions.status = filterStatus
          tmpOptions.page = 1
        }
        this.currentOptions = Object.assign({}, tmpOptions)
        this.$store.dispatch('account/fetch', tmpOptions)
      },

      editItem (item) {
        this.editedIndex = this.accounts.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.create = true
      },
      deleteItem (item) {
        this.editedIndex = this.accounts.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.dialog = true
      },
      processDeleteItem () {
        this.dialog = false
        const payload = {
          id: this.editedItem.id,
          options: this.currentOptions,
        }
        this.$store.dispatch('account/delete', payload)
        this.closeDialog()
      },
      onCreate () {
        this.errorPassword = ''
        this.errorEmail = ''
        const payload = {
          email: this.editedItem.email,
          password: this.editedItem.password,
          callback: this.handleCreated,
        }
        if (this.editedIndex === -1) {
          this.$store.dispatch('account/create', payload)
          this.onShowSnackBar('messages.please_wait', 10000)
        } else {
          delete payload.password
          this.$store.dispatch('account/edit', payload)
        }
      },

      closeDialog () {
        this.create = false
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      },
      closeCatchaDialog (result = false, response) {
        this.captchaDialog = false

        if (result) this.onResponseStatus(response.status, response)
        else this.onShowSnackBar('messages.captcha_failed')
      },
      closeOtpDialog () {
        this.otpDialog = false
      },

      handleCreated (result, success) {
        this.creating = false
        this.create = false
        this.snackbar = true

        this.result = success ? this.$t('messages.created_successfully') : result

        setTimeout(() => {
          this.snackbar = false
        })
      },
      hasErrors (errors) {
        return errors.length > 0
      },

      onDialogClose () {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
        this.polling = false

        this.$store.dispatch('account/clearStatus')
      },
      onShowSnackBar (value, timeout = 3000) {
        if (value === '') return
        this.snackbar = true
        this.result = this.$t(value)
        this.snackbarTimeout = timeout
        setTimeout(() => {
          this.snackbar = false
          this.$store.dispatch('account/clearMessage')
        }, timeout)
      },
      reLoginable (item) {
        return item.status !== 'ACTIVE'
      },
      createPolling (item) {
        this.pollingTime = 100
        this.pollingDialog = true
        this.polling = true
        if (this.timerPolling) {
          clearInterval(this.timerPolling)
        }
        this.timerPolling = setInterval(() => {
          this.handlePolling(item.id)
        }, 1000)
      },
      handlePolling (id) {
        // console.log('check')
        this.pollingTime -= 100 / this.defaultPollingTime
        this.$store.dispatch('account/createPolling', { id: id })
      },
      cancelPolling () {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
        // console.log('canceled')
        if (this.timerPolling) {
          clearInterval(this.timerPolling)
        }
        this.pollingDialog = false
        if (this.pollingStatus.status !== 'ACTIVE') {
          this.onShowSnackBar('messages.polling_canceled')
        }
      },
      reLogin (item) {
        this.editedIndex = this.accounts.indexOf(item)
        this.editedItem = Object.assign({}, item)
        // console.log('relogin')
        this.$store.dispatch('account/reCreate', { id: item.id })
      },
      filter () {
        if (this.status !== 'loading') {
          const isSearch = t
          this.initialize(isSearch)
        } else this.onShowSnackBar('messages.please_wait', 2000)
      },
      onResponseStatus (status, payload) {
        if (status !== 'ERROR') {
          if (status === 'CAPTCHA') {
            this.create = false
            this.captchaDialogData = {
              email: this.editedItem.email,
              message: payload.message,
            }
            this.captchaDialog = true
          }
          if (status === 'OTP') {
            this.create = false
            this.otpDialogData = {
              email: this.editedItem.email,
            }
            this.otpDialog = true
          }
          if (status === 'VERIFY') {
            // this.verifyDialog = true
            const id = payload.message ? payload.message : this.editedItem.id
            this.createPolling({ id: id })
            this.closeDialog()
          }
          if (status === 'SUCCESS') {
            this.closeDialog()
          }
          if (payload.message === 'CHANGE_PASSWORD') {
            this.changePasswordDialog = true
          }
        } else {
          if (payload.message === 'email_invalid') {
            this.errorEmail = this.$t('validator.email_invalid')
          }
          if (payload.message === 'password_invalid') {
            this.errorPassword = this.$t('messages.password_invalid')
          }
          if (payload.message === 'cant_login_amazon') {
            this.errorPassword = this.$t('validator.email_invalid')
            this.errorEmail = this.$t('messages.password_invalid')
          }
        }
      },
    },
  }
</script>
