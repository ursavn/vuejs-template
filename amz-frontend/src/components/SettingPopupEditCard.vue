<template>
  <validation-observer v-slot="{ handleSubmit }">
    <v-card
      v-if="loadingProduct"
      color="primary"
      dark
    >
      <v-card-text>
        {{ $t('messages.please_wait') }}
        <v-progress-linear
          indeterminate
          color="white"
          class="mb-0"
        />
      </v-card-text>
    </v-card>
    <v-form
      v-else
      ref="form"
      lazy-validation
      @submit.prevent="handleSubmit(onSubmit)"
    >
      <v-card
        class="mx-auto"
        light
        :loading="loading"
        style="transform-origin: center top 0;"
      >
        <v-card-title>
          <h2 class="mr-5">
            <span>{{ formTitle }}</span>
          </h2>
        </v-card-title>

        <v-card-text>
          <v-container>
            <!-- automation on off + asin-->
            <v-row>
              <v-col cols="6">
                <v-row>
                  <v-col cols="12">
                    <validation-provider
                      v-slot="{ errors }"
                      rules="required"
                    >
                      <v-text-field
                        v-model="setting.product.asin"
                        :error="hasErrors(errors)"
                        :label="$t('setting.labels.asin')"
                        readonly
                        disabled
                        type="text"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                  <v-col cols="12">
                    <v-textarea
                      v-model="setting.product.name"
                      name="product_name"
                      rows="2"
                      :label="$t('setting.labels.product_name')"
                      readonly
                      disabled
                    />
                  </v-col>
                </v-row>
              </v-col>
              <v-col cols="6">
                <v-card
                  outlined
                  :loading="loadingProduct"
                >
                  <v-img
                    contain
                    aspect-ratio="1.7"
                    :src="
                      setting.product.image
                        ? setting.product.image
                        : '/local-file-not-found.jpg'
                    "
                  />
                </v-card>
              </v-col>

              <!-- automation -->
              <!-- <v-col cols="6">
                <v-switch
                  v-model="setting.isOn"
                  inset
                  :label="$t('setting.labels.status')"
                />
              </v-col> -->
            </v-row>
            <!-- from to time pick  -->
            <v-row>
              <!-- timepicker -->
              <v-col cols="6">
                <v-menu
                  ref="fromTimeMenu"
                  v-model="startTimeMenu"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  :nudge-bottom="100"
                  :return-value.sync="fromTime"
                  transition="scale-transition"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <validation-provider
                      v-slot="{ errors }"
                      rules="required"
                    >
                      <v-text-field
                        v-model="fromTime"
                        autocomplete="off"
                        :error="hasErrors(errors)"
                        :label="$t('setting.labels.from_time')"
                        append-icon="mdi-clock-time-four-outline"
                        ampm-in-title
                        v-bind="attrs"
                        v-on="on"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </template>
                  <v-time-picker
                    v-if="startTimeMenu"
                    v-model="fromTime"
                    :max="toTime"
                    full-width
                    format="24hr"
                    @click:minute="$refs.fromTimeMenu.save(fromTime)"
                  />
                </v-menu>
              </v-col>
              <!-- timepicker -->
              <v-col cols="6">
                <v-menu
                  ref="toTimeMenu"
                  v-model="endTimeMenu"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  :nudge-bottom="100"
                  :return-value.sync="toTime"
                  transition="scale-transition"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <validation-provider
                      v-slot="{ errors }"
                      rules="required"
                    >
                      <v-text-field
                        v-model="toTime"
                        autocomplete="off"
                        :error="hasErrors(errors)"
                        :label="$t('setting.labels.from_time')"
                        append-icon="mdi-clock-time-four-outline"
                        ampm-in-title
                        v-bind="attrs"
                        v-on="on"
                      />
                      <span class="error--text">{{ errors[0] }}</span>
                      <span
                        v-show="toTimeReminder"
                        class="error--warning"
                      ><b>{{ toTimeReminder }}</b></span>
                    </validation-provider>
                  </template>
                  <v-time-picker
                    v-if="endTimeMenu"
                    v-model="toTime"
                    :min="fromTime"
                    full-width
                    format="24hr"
                    @click:minute="$refs.toTimeMenu.save(toTime)"
                  />
                </v-menu>
              </v-col>
            </v-row>
            <!-- account options -->
            <validation-provider
              v-slot="{ errors }"
              rules="required"
            >
              <v-row>
                <v-col cols="6">
                  <v-select
                    v-model="setting.accounts"
                    chips
                    multiple
                    item-text="email"
                    item-value="id"
                    :items="accountList"
                    :label="$t('setting.labels.account_options')"
                  />
                  <span class="error--text">{{ errors[0] }}</span>
                </v-col>
              </v-row>
            </validation-provider>
            <!-- Seller options -->
            <v-row>
              <v-col cols="6">
                <v-select
                  v-model="setting.sellerOption"
                  item-text="value"
                  item-value="key"
                  :items="sellerOptions"
                  :label="$t('setting.labels.seller_options')"
                  no-data-text="Sorry, nothing to display here :("
                />
              </v-col>

              <v-col
                v-show="setting.sellerOption === 'EXCLUDE_SELLER'"
                cols="6"
              >
                <v-select
                  v-model="setting.sellerOptionValue"
                  :items="sellersList"
                  :label="$t('setting.labels.except_sellers')"
                  multiple
                  item-text="sellerName"
                  item-value="sellerName"
                  chips
                  :no-data-text="$t('messages.list_empty')"
                />
              </v-col>

              <v-col
                v-show="setting.sellerOption === 'EXCLUDE_RATE_LOWER'"
                cols="6"
              >
                <v-text-field
                  v-model="setting.sellerOptionValue"
                  name="rating"
                  :label="$t('setting.labels.rating')"
                  type="number"
                  hint="Ex: 4.0"
                  min="0"
                  max="5"
                  step="0.0001"
                  oninput="validity.valid||(value='');"
                />
              </v-col>
            </v-row>
            <!-- ASIN input -->

            <!-- Quantity -->
            <validation-provider
              v-slot="{ errors }"
              rules="required"
            >
              <v-row>
                <v-col cols="6">
                  <v-text-field
                    v-model="setting.quantity"
                    :disabled="loading"
                    :error="hasErrors(errors)"
                    autocomplete="false"
                    :label="$t('setting.labels.quantity')"
                    type="number"
                    min="1"
                    step="1"
                    oninput="validity.valid||(value='');"
                  />
                  <span class="error--text">{{ errors[0] }}</span>
                </v-col>
                <v-col cols="3">
                  <v-select
                    v-model="setting.quantityOption"
                    item-text="value"
                    item-value="key"
                    :items="quantityOptions"
                    autocomplete="false"
                    :label="$t('setting.labels.quantity_options')"
                  />
                </v-col>
              </v-row>
            </validation-provider>

            <!-- Price, less then buy -->
            <validation-provider
              v-slot="{ errors }"
              rules="required"
            >
              <v-row>
                <v-col cols="6">
                  <v-text-field
                    v-model="setting.price"
                    autocomplete="off"
                    :disabled="loading"
                    :error="hasErrors(errors)"
                    :label="$t('setting.labels.max_price')"
                    type="number"
                    min="1"
                    step="1"
                    oninput="validity.valid||(value='');"
                  />
                  <span class="error--text">{{ errors[0] }}</span>
                </v-col>
                <v-col cols="3">
                  <v-select
                    v-model="setting.priceOption"
                    item-text="value"
                    item-value="key"
                    autocomplete="off"
                    :items="priceOptions"
                    :label="$t('setting.labels.price_options')"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="3">
                  <v-checkbox
                    v-model="setting.primeOnly"
                    :label="$t('setting.labels.prime_only')"
                  />
                </v-col>
                <v-col cols="3">
                  <v-checkbox
                    v-model="setting.status"
                    :label="$t('setting.labels.automation')"
                  />
                </v-col>
              </v-row>
            </validation-provider>
          </v-container>
        </v-card-text>
        <v-card-actions class="d-flex align-center justify-center">
          <v-btn
            light
            text
            large
            color="error"
            class="mr-4"
            :disabled="loading"
            @click="$emit('close')"
          >
            {{ $t('messages.cancel') }}
          </v-btn>
          <v-btn
            light
            text
            large
            color="primary"
            type="submit"
            class="mr-4"
            depressed
            :disabled="loading"
          >
            {{ $t('messages.save') }}
          </v-btn>
        </v-card-actions>
      </v-card>
      <v-snackbar
        v-model="productSnackBar"
        :timeout="snackBarTimeout"
      >
        {{ result }}

        <template v-slot:action="{ attrs }">
          <v-btn
            color="pink"
            text
            v-bind="attrs"
            @click="productSnackBar = false"
          >
            {{ $t('messages.close') }}
          </v-btn>
        </template>
      </v-snackbar>
    </v-form>
  </validation-observer>
</template>

<script>
  import { ValidationProvider, ValidationObserver, extend } from 'vee-validate'
  import { required, email, min } from 'vee-validate/dist/rules'
  import { get } from 'vuex-pathify'
  export default {
    name: 'SettingEditPopupCard',
    components: {
      ValidationProvider,
      ValidationObserver,
    },
    props: {
      close: Function,
      formTitle: String,
      currentSetting: Object,
      listOptions: Object,
    },

    data: () => {
      return {
        date: new Date().toISOString().substr(0, 10),
        loading: false,
        errors: [],
        startTimeMenu: false,
        endTimeMenu: false,
        startDateMenu: false,
        fromTime: '',
        toTime: '',
        endDateMenu: false,
        sellerOptions: [],
        sellersList: [],
        priceOptions: [],
        quantityOptions: [],
        quantityOptionWith: 100,
        product: {},
        setting: {},
        loadingProduct: false,
        asinError: false,
        result: '',
        productSnackBar: false,
        snackBarTimeout: 6000,
        currentOptions: {},
        toTimeReminder: '',
      }
    },
    computed: {
      ...get('automation', [
        'productStatus',
        'addedProduct',
        'message',
        'status',
        'messageProduct',
      ]),
      accountList: get('account@list.data'),
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
      },
      fromTime: function (val) {
        const timeArray = val.split(':')
        const timeInMinutes = parseInt(timeArray[0]) * 60 + parseInt(timeArray[1])
        this.setting.fromTime = timeInMinutes
      },
      toTime: function (val) {
        const timeArray = val.split(':')
        const timeInMinutes = parseInt(timeArray[0]) * 60 + parseInt(timeArray[1])
        this.setting.toTime = timeInMinutes === 0 ? 1440 : timeInMinutes
        this.toTimeReminder =
          this.currentSetting.toTime === 1440
            ? this.$t('messages.to_time_24')
            : ''
      },
      'setting.sellerOption': function (val, oldVal) {
        if (oldVal && oldVal !== '') {
          if (
            val === 'EXCLUDE_SELLER' &&
            this.setting.sellerOptionValue !== '' &&
            oldVal !== 'EXCLUDE_SELLER'
          ) {
            this.setting.sellerOptionValue = ''
          }
          // component created

          if (
            oldVal === 'EXCLUDE_SELLER' &&
            this.setting.sellerOptionValue !== ''
          ) {
            this.setting.sellerOptionValue = ''
          }
        }
      },
      // 'currentSetting.quantityOption': function () {
      //   this.currentSetting.quantityOptionValue = ''
      // },
      // 'currentSetting.prodcuct': function () {
      //   this.currentSetting.quantityOptionValue = ''
      // },
      addedProduct (value) {
        this.sellersList = value.sellers
        this.setting.product = value
      },
      productStatus (value) {
        this.loadingProduct = value === 'loading'
        this.asinError = false
      },
      messageProduct (value) {
        if (value === '') return
        this.productSnackBar = true
        this.result = this.$t(value)
        if (value !== 'messages.completed' && this.productStatus !== 'loading') {
          this.asinError = true
          this.$store.dispatch('automation/setMessage', {
            message: 'messages.server_error',
          })
        }
        if (value !== 'messages.finding_product') {
          setTimeout(() => {
            this.$store.dispatch('automation/clearProductMessage')
            this.productSnackBar = false
          }, 4000)
        }
      },
      listOptions (value) {
        this.currentOptions = Object.assign({}, value)
      },
      currentSetting (value) {
        this.$store.dispatch('account/fetch')
        this.setting = value
        if (value.product.asin) {
          this.$store.dispatch('automation/fetchProduct', {
            asin: this.setting.product.asin,
          })
        }
        if (value.sellerOption === 'EXCLUDE_SELLER' && value.sellerOptionValue) {
          const arrOptValue = value.sellerOptionValue.split(',')
          this.setting.sellerOptionValue = arrOptValue
        }
        if (this.setting.accounts) {
          const accountsValue = this.setting.accounts.split(',').map(function (x) {
            return parseInt(x, 10)
          })
          this.setting.accounts = accountsValue
        }

        this.toTime = this.minuesToTime(this.setting.toTime)
        this.fromTime = this.minuesToTime(this.setting.fromTime)
      },
    },
    created () {
      this.$store.dispatch('account/fetch')
      this.setting = this.currentSetting
      if (
        this.setting.sellerOption === 'EXCLUDE_SELLER' &&
        this.setting.sellerOptionValue
      ) {
        const arrOptValue = this.currentSetting.sellerOptionValue.split(',')
        this.setting.sellerOptionValue = arrOptValue
      }
      if (this.setting.accounts) {
        const accountsValue = this.setting.accounts.split(',').map(function (x) {
          return parseInt(x, 10)
        })

        this.setting.accounts = accountsValue
      }

      this.findProduct()
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
        message: this.$t('validator.10_characters'),
      })
    },

    mounted () {
      this.sellerOptions = Object.entries(
        this.$t('setting.seller_options'),
      ).map(([key, value]) => ({ key, value }))
      this.quantityOptions = Object.entries(
        this.$t('setting.quantityOptions'),
      ).map(([key, value]) => ({ key, value }))
      this.priceOptions = Object.entries(
        this.$t('setting.priceOptions'),
      ).map(([key, value]) => ({ key, value }))
      this.toTime = this.minuesToTime(this.setting.toTime)
      this.fromTime = this.minuesToTime(this.setting.fromTime)
    // need seller options
    },
    methods: {
      minuesToTime (val) {
        let minues = val % 60
        let hours = (val - minues) / 60
        if (hours === 24 && minues === 0) hours = 0
        minues = minues < 10 ? '0' + minues : minues
        return hours + ':' + minues
      },
      onSubmit () {
        const tempSetting = { ...this.setting }
        if (
          tempSetting.sellerOption === 'EXCLUDE_SELLER' &&
          Array.isArray(tempSetting.sellerOptionValue)
        ) {
          let excludeOptValue = ''
          tempSetting.sellerOptionValue.forEach(
            element => (excludeOptValue += element + ','),
          )
          excludeOptValue = excludeOptValue.substring(
            0,
            excludeOptValue.length - 1,
          )
          tempSetting.sellerOptionValue = excludeOptValue
        }
        if (tempSetting.accounts) {
          let accountsValue = ''
          tempSetting.accounts.forEach(
            element => (accountsValue += element + ','),
          )
          accountsValue = accountsValue.substring(0, accountsValue.length - 1)
          tempSetting.accounts = accountsValue
        }
        tempSetting.options = this.listOptions
        this.$store.dispatch('automation/update', tempSetting)
      },
      fetchProduct (asin) {
        this.$store.dispatch('automation/fetchProduct', {
          asin: asin,
        })
      },
      findProduct () {
        if (this.setting.product.asin.length === 10) {
          this.product = Object.assign({}, {})
          this.$store.dispatch('automation/fetchProduct', {
            asin: this.setting.product.asin,
          })
        }
      },
      hasErrors (errors) {
        return errors.length > 0
      },
    },
  }
</script>

<style></style>
