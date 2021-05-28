<template>
  <div>
    <material-card
      color="primary"
      icon="mdi-receipt"
    >
      <v-data-table
        :headers="headers"
        :items="orders"
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
        <template v-slot:item="props">
          <tr>
            <td width="8%">
              {{ props.item.id }}
            </td>
            <td>
              <img
                width="90px"
                height="110px"
                :src="props.item.product.image"
                alt="@/assets/local-file-not-found.png"
              >
            </td>
            <td width="30%">
              <span text-truncate>{{ props.item.product.name }}</span>
            </td>
            <td width="16%">
              <span>{{
                props.item.sellerName ? props.item.sellerName : ''
              }}</span>
            </td>
            <td width="10%">
              {{ $n(props.item.price, 'currency') }}
            </td>
            <td width="12%">
              {{ props.item.createdDate | formatDate }}
            </td>
            <td>
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon
                    small
                    class="mr-2"
                    v-bind="attrs"
                    v-on="on"
                    @click="detailItem(props.item)"
                  >
                    mdi-details
                  </v-icon>
                </template>
                <span>{{ $t('v-table.tooltip.detail') }}</span>
              </v-tooltip>
            </td>
          </tr>
        </template>

        <template v-slot:top>
          <v-toolbar
            flat
            class="text-center"
          >
            <v-container>
              <v-row class="mt-5">
                <v-col cols="3">
                  <v-toolbar-title class="float-left">
                    {{ $t('order.title') }}
                  </v-toolbar-title>
                </v-col>
                <v-col
                  cols="4"
                  md="4"
                >
                  <v-text-field
                    v-model="filterAsin"
                    hide-details
                    prepend-icon="mdi-magnify"
                    :placeholder="$t('messages.filter_asin')"
                    single-line
                    max-width="50px"
                    @keyup.enter="filterSearch"
                    @click:prepend="filterSearch"
                  />
                </v-col>
              </v-row>
            </v-container>
            <v-dialog
              v-model="detailDialog"
              max-width="800px"
            >
              <!-- add/edit dialog -->
              <order-popup-detail
                v-show="detailDialog"
                :order="getEditedItem"
                @close="closeDialog()"
              />

              <!--/ add/edit dialog -->
            </v-dialog>
          </v-toolbar>
        </template>
        <template v-slot:footer.page-text="items">
          {{ items.pageStart }} - {{ items.pageStop }}
          {{ $t('v-table.headers.page-text') }}
          {{ items.itemsLength }}
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
    </material-card>
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
  </div>
</template>

<script>
  import OrderPopupDetail from '../components/OrderPopupDetail'
  import { get } from 'vuex-pathify'
  export default {
    name: 'OrderList',
    components: {
      OrderPopupDetail: OrderPopupDetail,
    },
    data: () => {
      return {
        options: {},
        orders: [],
        headers: [],
        editedIndex: -1,
        editedItem: {},
        defaultItem: {
          product: {},
          seller: {},
          amazonAccount: {},
          id: '',
          email: '',
          createdDate: '',
          modifiedDate: '',
        },
        currentPage: 1,
        totalItems: 1,
        totalPages: 1,
        loading: false,
        filterAsin: '',
        snackbar: false,
        result: '',
        detailDialog: false,
        snackbarTimeout: 5000,
      }
    },
    computed: {
      ...get('order', ['list', 'status', 'message']),
      getEditedItem () {
        return this.editedItem
      },
    },
    watch: {
      status (value) {
        this.loading = value === 'loading'
      },

      message (value) {
        this.onShowSnackBar(value)
      },
      list (value) {
        this.$set(this, 'orders', value.data)
        this.currentPage = value.currentPage
        this.totalItems = value.totalItems
        this.totalPages = value.totalPages
        this.totalPages = value.totalPages
      },
      // fake du lieu
      options: {
        handler () {
          // this.orders = this.fakeOrders
          this.initialize()
        },
        deep: true,
      },
    },
    mounted () {
      this.headers = [
        {
          text: this.$t('v-table.headers.id'),
          align: 'start',
          value: 'id',
        },
        { text: this.$t('v-table.headers.image'), value: 'product.image' },
        {
          text: this.$t('v-table.headers.product_name'),
          value: 'amazonAccount.email',
        },
        {
          text: this.$t('v-table.headers.seller'),
          value: 'seller.name',
        },
        {
          text: this.$t('v-table.headers.price'),
          value: 'price',
        },
        { text: this.$t('v-table.headers.created_date'), value: 'createdDate' },
        {
          text: this.$t('v-table.headers.detail'),
          value: 'actions',
          sortable: false,
        },
      ]
    },
    methods: {
      initialize (isSearch = true) {
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
        if (this.filterAsin) {
          if (!isSearch) {
            tmpOptions.page = 1
            this.options.page = 1
          }
          tmpOptions.search = this.filterAsin
        }
        this.$store.dispatch('order/fetch', tmpOptions)
      },

      detailItem (item) {
        this.editedIndex = this.orders.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.detailDialog = true
      },

      closeDialog () {
        this.detailDialog = false
        this.editedItem = this.defaultItem
        this.editedIndex = -1
      },

      hasErrors (errors) {
        return errors.length > 0
      },

      onShowSnackBar (value, timeout = 3000) {
        if (value === '') return
        this.snackbar = true
        this.result = this.$t(value)
        this.snackbarTimeout = timeout
        setTimeout(() => {
          this.snackbar = false
          this.$store.dispatch('order/clearMessage')
        }, timeout)
      },

      filterSearch (value, oldValue) {
        if (this.status !== 'loading') {
          const isSearch = value === oldValue
          this.initialize(isSearch)
        } else this.onShowSnackBar('messages.please_wait', 2000)
      },
    },
  }
</script>
