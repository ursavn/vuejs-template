<template>
  <div>
    <v-container>
      <v-row>
        <v-col>
          <v-card>
            <v-card-title>
              <h2>
                <span>{{ $t('order.detail.title') }}</span>
              </h2>
            </v-card-title>
            <v-spacer />
            <v-divider class="mt-5 mb-5" />
            <v-card-text>
              <v-row>
                <v-col>
                  <h4>{{ $t('order.detail.product_title') }}</h4>
                </v-col>
              </v-row>
              <v-row>
                <v-col cols="4">
                  <v-img
                    contain
                    aspect-ratio="1"
                    :src="
                      currentOrder.product.image
                        ? currentOrder.product.image
                        : '/local-file-not-found.jpg'
                    "
                  />
                </v-col>
                <v-col cols="8">
                  <v-row>
                    <v-col :cols="labelCol">
                      {{ $t('order.detail.asin') }}
                    </v-col>
                    <v-col>{{ currentOrder.product.asin }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col :cols="labelCol">
                      {{ $t('order.detail.name') }}
                    </v-col>
                    <v-col>{{ currentOrder.product.name }}</v-col>
                  </v-row>
                  <v-row>
                    <v-col :cols="labelCol">
                      {{ $t('order.detail.rating') }}
                    </v-col>
                    <v-col>
                      <span>
                        <v-rating
                          v-model="currentOrder.product.rate"
                          half-increments
                          size="8"
                          readonly
                          background-color="orange lighten-3"
                          color="orange"
                          x-small
                        />
                      </span>
                    </v-col>
                  </v-row>
                  <!-- <v-row>
                    <v-col :cols="labelCol">
                      {{ $t('order.detail.price') }}
                    </v-col>
                    <v-col>
                      {{ $n(currentOrder.product.price, 'currency') }}
                    </v-col>
                  </v-row> -->
                </v-col>
              </v-row>
              <v-divider class="mt-5 mb-5" />
              <v-row>
                <v-col>
                  <v-row>
                    <v-col>
                      <h2>
                        <span> {{ $t('order.detail.total_detail') }}</span>
                      </h2>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col cols="ml-3">
                      <v-row>
                        <v-col :cols="3">
                          <span>{{ $t('order.detail.quantity') }}</span>
                        </v-col>
                        <v-col cols="4">
                          {{ currentOrder.quantity }}
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col :cols="3">
                          <span>{{ $t('order.detail.ordered_price') }}</span>
                        </v-col>
                        <v-col cols="4">
                          {{ $n(currentOrder.price, 'currency') }}
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col :cols="3">
                          <span>{{ $t('order.detail.point') }}</span>
                        </v-col>
                        <v-col cols="4">
                          {{ currentOrder.point || 0 }}
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col :cols="3">
                          <span>{{ $t('order.detail.coupon') }}</span>
                        </v-col>
                        <v-col cols="4">
                          {{ currentOrder.coupon || $t('messages.none') }}
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col :cols="3">
                          <span>{{ $t('order.detail.total') }}</span>
                        </v-col>
                        <v-col cols="4">
                          <span><h4>
                            {{
                              $n(
                                currentOrder.price * currentOrder.quantity -
                                  currentOrder.coupon ||
                                  0 - currentOrder.point ||
                                  0,
                                'currency'
                              )
                            }}
                          </h4></span>
                        </v-col>
                      </v-row>
                    </v-col>
                    <!-- --- -->
                    <v-col offset="1">
                      <v-row>
                        <v-col :cols="subLabelCol">
                          <h4>
                            <span>{{ $t('order.detail.account_title') }}</span>
                          </h4>
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col :cols="subLabelCol - 2">
                          <span>{{ $t('order.detail.email') }}</span>
                        </v-col>
                        <v-col>
                          <span>{{ currentOrder.amazonAccountEmail }}</span>
                        </v-col>
                      </v-row>
                      <v-divider class="mt-5 mb-5" />
                      <v-row>
                        <v-col :cols="subLabelCol">
                          <h4>
                            <span>{{ $t('order.detail.seller_title') }}</span>
                          </h4>
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col :cols="subLabelCol - 2">
                          <span>{{ $t('order.detail.name') }}</span>
                        </v-col>
                        <v-col>
                          <span>{{
                            currentOrder.sellerName
                              ? currentOrder.sellerName
                              : ''
                          }}</span>
                        </v-col>
                      </v-row>
                    </v-col>
                  </v-row>
                </v-col>
              </v-row>
            </v-card-text>
            <v-card-actions class="d-flex align-center justify-center">
              <v-btn
                light
                text
                large
                color="error"
                class="mr-4"
                @click="$emit('close')"
              >
                {{ $t('messages.close') }}
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
  import { get } from 'vuex-pathify'
  export default {
    name: 'SettingEditPopupCard',
    props: {
      close: Function,
      order: Object,
    },

    data: () => {
      return {
        loading: false,
        product: {},
        currentOrder: {},
        result: '',
        productSnackBar: false,
        snackBarTimeout: 6000,
        labelCol: 2,
        subLabelCol: 5,
      }
    },
    computed: {
      ...get('order', ['message', 'status', 'messageProduct']),
      totalProductPrice () {
        return this.currentOrder.product.price * this.currentOrder.quantity
      },
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
      },
      order: {
        immediate: true,
        handler (value) {
          this.$set(this, 'currentOrder', value)
        },
      },
    },
    created () {},

    mounted () {},
    methods: {},
  }
</script>

<style></style>
