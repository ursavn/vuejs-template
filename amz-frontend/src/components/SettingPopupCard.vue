<template>
  <validation-observer v-slot="{ handleSubmit }">
    <v-form
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
                        v-model="currentSetting.asin"
                        :disabled="loadingProduct"
                        :error="asinError"
                        :label="$t('setting.labels.asin')"
                        type="text"
                      />
                      <span
                        v-if="asinError"
                        class="error--text"
                      >{{
                        $t('messages.cant_find_product')
                      }}</span>
                      <span class="error--text">{{ errors[0] }}</span>
                    </validation-provider>
                  </v-col>
                  <!-- <v-col cols="4">
                    <v-btn @click="findProduct()">
                      {{ $t('messages.search') }}
                    </v-btn>
                  </v-col> -->

                  <v-col cols="12">
                    <v-textarea
                      v-model="product.name"
                      name="product_name"
                      rows="2"
                      :label="$t('setting.labels.product_name')"
                      disabled
                      readonly
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
                      product.image
                        ? product.image
                        : '/local-file-not-found.jpg'
                    "
                  />
                </v-card>
              </v-col>

              <!-- automation -->
              <!-- <v-col cols="6">
                <v-switch
                  v-model="currentSetting.isOn"
                  inset
                  :label="$t('setting.labels.status')"
                />
              </v-col> -->
            </v-row>

            <!-- from to time pick  -->
            <v-row>
              <v-col cols="6">
                <!-- timepicker -->
                <v-menu
                  ref="fromTimeMenu"
                  v-model="startTimeMenu"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  :nudge-bottom="100"
                  :return-value.sync="fromTime"
                  transition="slide-x-transition"
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
                    full-width
                    format="24hr"
                    :max="toTime"
                    @click:minute="$refs.fromTimeMenu.save(fromTime)"
                  />
                </v-menu>
              </v-col>

              <v-col cols="6">
                <!-- timepicker -->
                <v-menu
                  ref="toTimeMenu"
                  v-model="endTimeMenu"
                  :close-on-content-click="false"
                  :nudge-right="40"
                  :nudge-bottom="100"
                  :return-value.sync="toTime"
                  transition="slide-x-transition"
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
            <!-- account opstions -->
            <validation-provider
              v-slot="{ errors }"
              rules="required"
            >
              <v-row>
                <v-col cols="6">
                  <v-select
                    v-model="currentSetting.accounts"
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
                  v-model="currentSetting.sellerOption"
                  item-text="value"
                  :disabled="loadingProduct"
                  item-value="key"
                  :items="sellerOptions"
                  :label="$t('setting.labels.seller_options')"
                />
              </v-col>

              <v-col
                v-show="currentSetting.sellerOption === 'EXCLUDE_SELLER'"
                cols="6"
              >
                <v-select
                  v-model="currentSetting.sellerOptionValue"
                  :disabled="loadingProduct"
                  :items="sellersList"
                  item-text="sellerName"
                  item-value="sellerName"
                  :label="$t('setting.labels.except_sellers')"
                  multiple
                  chips
                  :no-data-text="$t('messages.list_empty')"
                />
              </v-col>

              <v-col
                v-show="currentSetting.sellerOption === 'EXCLUDE_RATE_LOWER'"
                cols="6"
              >
                <v-text-field
                  v-model="currentSetting.sellerOptionValue"
                  :disabled="loadingProduct"
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
                    v-model="currentSetting.quantity"
                    :disabled="loading"
                    autocomplete="off"
                    :error="hasErrors(errors)"
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
                    v-model="currentSetting.quantityOption"
                    item-text="value"
                    item-value="key"
                    autocomplete="off"
                    :items="quantityOptions"
                    :label="$t('setting.labels.quantity_options')"
                  />
                </v-col>
                <!-- <v-col
                  v-show="currentSetting.quantityOption === 'MAX_QUANTITY'"
                  cols="3"
                >
                  <v-text-field
                    v-model="currentSetting.quantityOptionValue"
                    :label="$t('setting.labels.max_amount')"
                  />
                </v-col> -->
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
                    v-model="currentSetting.price"
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
                    v-model="currentSetting.priceOption"
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
                    v-model="currentSetting.primeOnly"
                    :label="$t('setting.labels.prime_only')"
                  />
                </v-col>
                <v-col cols="3">
                  <v-checkbox
                    v-model="currentSetting.status"
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
            @click="onClose()"
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
    </v-form>
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
  </validation-observer>
</template>

<script>
  import { ValidationProvider, ValidationObserver, extend } from 'vee-validate'
  import { required, email, min } from 'vee-validate/dist/rules'
  import { get } from 'vuex-pathify'

  export default {
    name: 'SettingPopupCard',
    components: {
      ValidationProvider,
      ValidationObserver,
    },
    props: {
      formTitle: String,
      close: Function,
    },

    data: () => {
      return {
        date: new Date().toISOString().substr(0, 10),
        loadingProduct: false,
        productSnackBar: false,
        loading: false,
        errors: [],
        startTimeMenu: false,
        endTimeMenu: false,
        startDateMenu: false,
        fromTime: '',
        toTime: '',
        image: '',
        endDateMenu: false,
        sellerOptions: [],
        priceOptions: [],
        quantityOptions: [],
        sellersList: [],
        priceOption: [],
        quantityOptionWith: 100,
        currentSetting: {},
        product: {},
        defualtSetting: {
          asin: '',
          productId: '',
          fromTime: '',
          toTime: '',
          primeOnly: false,
          sellerOption: null,
          sellerOptionValue: null,
          price: null,
          quantity: null,
          quantityOption: null,
          quantityOptionValue: null,
        },
        result: '',
        snackBarTimeout: 6000,
        asinError: false,
        defaultRating: 4.0,
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
      productStatus (val) {
        this.loadingProduct = val === 'loading'
        this.asinError = false
      },
      addedProduct (val) {
        this.product = val
        this.sellersList = val.sellers
        this.currentSetting.productId = val.id
      },
      status (value) {
        this.loading = value === 'loading'
      },
      'currentSetting.asin': function (val) {
        this.findProduct()
      },
      fromTime: function (val) {
        const timeArray = val.split(':')
        const timeInMinutes = parseInt(timeArray[0]) * 60 + parseInt(timeArray[1])
        this.currentSetting.fromTime = timeInMinutes
      },
      toTime: function (val) {
        const timeArray = val.split(':')
        const timeInMinutes = parseInt(timeArray[0]) * 60 + parseInt(timeArray[1])
        this.currentSetting.toTime = timeInMinutes === 0 ? 1440 : timeInMinutes
        this.toTimeReminder =
          this.currentSetting.toTime === 1440
            ? this.$t('messages.to_time_24')
            : ''
      },
      'currentSetting.sellerOption': function () {
        if (this.currentSetting.sellerOption === 'EXCLUDE_RATE_LOWER') {
          this.currentSetting.sellerOptionValue = this.defaultRating
        } else {
          this.currentSetting.sellerOptionValue = ''
        }
      },
      'currentSetting.quantityOption': function () {
        this.currentSetting.quantityOptionValue = ''
      },
      message (value) {
        if (value === 'messages.save_successfully') {
          this.onClose()
        }
      },
      messageProduct (value) {
        if (value === '') return
        this.productSnackBar = true
        this.result = this.$t(value)
        if (value !== 'messages.completed' && this.productStatus !== 'loading') {
          this.asinError = true
        }
        if (value !== 'messages.finding_product') {
          setTimeout(() => {
            this.$store.dispatch('automation/clearProductMessage')
            this.productSnackBar = false
          }, 10000)
        }
      },
    },
    created () {
      this.$store.dispatch('account/fetch')
      this.currentSetting = Object.assign({}, this.defualtSetting)
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
      this.image = this.product.image
      this.sellerOptions = Object.entries(
        this.$t('setting.seller_options'),
      ).map(([key, value]) => ({ key, value }))
      this.currentSetting.sellerOption = this.sellerOptions[0].key
      this.quantityOptions = Object.entries(
        this.$t('setting.quantityOptions'),
      ).map(([key, value]) => ({ key, value }))
      this.currentSetting.quantityOption = this.quantityOptions[0].key
      this.priceOptions = Object.entries(
        this.$t('setting.priceOptions'),
      ).map(([key, value]) => ({ key, value }))
      this.currentSetting.priceOption = this.priceOptions[0].key

    // need seller options
    },
    methods: {
      findProduct () {
        if (this.currentSetting.asin.length === 10) {
          this.product = Object.assign({}, {})
          this.$store.dispatch('automation/fetchProduct', {
            asin: this.currentSetting.asin,
          })
        }
      },
      imgNotFound () {
        this.image = '/local-file-not-found.jpg'
      },
      onSubmit () {
        const tempSetting = { ...this.currentSetting }
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
          // console.log(excludeOptValue.slice(0, excludeOptValue.lastIndexOf(',')))
          tempSetting.sellerOptionValue = excludeOptValue
        }
        if (tempSetting.accounts) {
          let accountsValue = ''
          tempSetting.accounts.forEach(
            element => (accountsValue += element + ','),
          )
          accountsValue = accountsValue.substring(0, accountsValue.length - 1)
          tempSetting.accounts = accountsValue
          // console.log(excludeOptValue.slice(0, excludeOptValue.lastIndexOf(',')))
          tempSetting.accounts = accountsValue
        }
        this.$store.dispatch('automation/create', tempSetting)
      },
      hasErrors (errors) {
        return errors.length > 0
      },
      onClose () {
        this.fromTime = ''
        this.fromTime = ''
        this.product = Object.assign({}, {})
        this.currentSetting = Object.assign({}, this.defualtSetting)
        this.$emit('close')
      },
    },
  }
</script>

<style></style>
