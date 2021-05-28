<template>
  <material-card
    color="primary"
    icon="mdi-auto-upload"
  >
    <v-data-table
      :headers="headers"
      :items="automations"
      :options.sync="options"
      :server-items-length="totalItems"
      :loading="loading"
      class="elevation-1"
      :loading-text="$t('messages.loading_item')"
      :footer-props="{
        itemsPerPageOptions: [5, 10, 15, 100],
        showFirstLastPage: true,
        'items-per-page-text': $t('v-table.headers.items-per-page'),
      }"
    >
      <template v-slot:item="props">
        <tr>
          <td>{{ props.index + 1 }}</td>
          <td width="6%">
            <img
              width="90px"
              height="110px"
              :src="props.item.product.image"
              alt="@/assets/local-file-not-found.png"
            >
          </td>
          <td width="14%">
            <span text-truncate>{{ props.item.product.name }}</span>
          </td>

          <td width="7%">
            {{ minuesToTime(props.item.fromTime) }}
          </td>
          <td width="7%">
            {{ minuesToTime(props.item.toTime) }}
          </td>
          <td width="6%">
            <span>{{ props.item.quantity }}</span>
          </td>
          <td width="8%">
            {{ $n(props.item.price, 'currency') }}
          </td>

          <td width="10%">
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <div
                  class="d-inline-flex"
                  v-bind="attrs"
                  v-on="on"
                >
                  <v-switch
                    v-model="props.item.status"
                    flat
                    @click="changeStatus(props.item)"
                  />
                </div>
              </template>
              <span>{{ $t('v-table.tooltip.status') }}</span>
            </v-tooltip>
          </td>
          <td width="10%">
            <span class="primary--text">
              {{ props.item.purchaseQuantity || 0 }}</span>
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
                  @click="editItem(props.item)"
                >
                  mdi-pencil
                </v-icon>
              </template>
              <span>{{ $t('v-table.tooltip.edit') }}</span>
            </v-tooltip>

            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                  small
                  class="mr-2"
                  v-bind="attrs"
                  v-on="on"
                  @click="deleteItem(props.item)"
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
                  v-on="on"
                  @click="openItemLog(props.item)"
                >
                  mdi-note-text
                </v-icon>
              </template>
              <span>{{ $t('v-table.tooltip.log') }}</span>
            </v-tooltip>
          </td>
        </tr>
      </template>
      <template v-slot:footer.page-text="items">
        {{ items.pageStart }} - {{ items.pageStop }}
        {{ $t('v-table.headers.page-text') }}
        {{ items.itemsLength }}
      </template>
      <template slot="no-data">
        {{ $t('messages.list_empty') }}
      </template>

      <template v-slot:top>
        <v-toolbar
          flat
          class="text-center"
        >
          <v-container class="mt-5">
            <v-row>
              <v-col cols="3">
                <v-toolbar-title class="float-left">
                  {{ $t('setting.messages.settings') }}
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
              <v-col cols="3">
                <v-checkbox
                  v-model="isFilterStatus"
                  :label="$t('messages.filter_auto')"
                />
              </v-col>
              <v-col>
                <v-dialog
                  v-model="dialog"
                  max-width="700px"
                  persistent
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-btn
                      color="primary"
                      dark
                      class="mb-2 float-right"
                      v-bind="attrs"
                      v-on="on"
                    >
                      {{ $t('setting.messages.new_setting') }}
                    </v-btn>
                  </template>
                  <!-- add/edit dialog -->
                  <setting-popup-card
                    :form-title="formTitle"
                    @close="close()"
                  />

                  <!--/ add/edit dialog -->
                </v-dialog>
              </v-col>
            </v-row>
          </v-container>

          <v-spacer />

          <v-dialog
            v-model="editDialog"
            max-width="700px"
            persistent
          >
            <!-- add/edit dialog -->
            <setting-popup-edit-card
              v-show="editDialog"
              :form-title="formTitle"
              :current-setting="getEditedItem"
              :list-options="currentOptions"
              @close="close()"
            />

            <!--/ add/edit dialog -->
          </v-dialog>
          <v-spacer />
          <v-dialog
            v-model="logsDialog"
            max-width="900px"
          >
            <v-card>
              <v-card-text class="text-center mt-5">
                <setting-popup-log
                  v-show="logsDialog"
                  :current-setting="getEditedItem"
                  @close="close()"
                />
              </v-card-text>
              <v-card-actions>
                <v-spacer />
                <v-btn
                  color="red darken-1"
                  text
                  @click="close()"
                >
                  {{ $t('messages.close') }}
                </v-btn>
              </v-card-actions>
            </v-card>
            <!-- add/edit dialog -->

            <!--/ add/edit dialog -->
          </v-dialog>
          <!-- delete dialog -->
          <v-dialog
            v-model="dialogDelete"
            max-width="500px"
          >
            <v-card>
              <v-card-title>
                <span
                  v-html="
                    $t('setting.messages.do_you_want_to_delete_setting', {
                      asin: editedItem.product.asin,
                    })
                  "
                />
              </v-card-title>
              <v-card-actions>
                <v-spacer />
                <v-btn
                  color="green darken-1"
                  text
                  @click="closeDelete"
                >
                  {{ $t('messages.no') }}
                </v-btn>
                <v-btn
                  color="red darken-1"
                  text
                  @click="deleteItemConfirm"
                >
                  {{ $t('messages.yes') }}
                </v-btn>
                <v-spacer />
              </v-card-actions>
            </v-card>
          </v-dialog>
          <!--/ delete dialog -->
        </v-toolbar>
      </template>
      <template v-slot:item.isOn="{ item }">
        <v-switch
          v-model="item.isOn"
          readonly
        />
      </template>
    </v-data-table>
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
  </material-card>
</template>
<script>
  import { get } from 'vuex-pathify'
  import SettingPopupEditCard from '../components/SettingPopupEditCard'
  import SettingPopupCard from '../components/SettingPopupCard'
  import SettingPopupLog from '../components/SettingPopupLog'
  export default {
    name: 'AutomationList',
    components: {
      settingPopupEditCard: SettingPopupEditCard,
      settingPopupCard: SettingPopupCard,
      SettingPopupLog: SettingPopupLog,
    },
    data: () => ({
      snackbar: false,
      options: {},
      dialog: false,
      editDialog: false,
      dialogDelete: false,
      totalRecord: null,
      editedIndex: -1,
      editedItem: {},
      defaultItem: {
        product: {},
        sellerOptionValue: '',
        sellerOption: '',
        accounts: '',
      },
      headers: [],
      automations: [],
      currentPage: 0,
      totalItems: 0,
      totalPages: 0,
      loading: false,
      result: '',
      filterAsin: '',
      isFilterStatus: false,
      logsDialog: false,
      currentOptions: {},
    }),
    computed: {
      formTitle () {
        return this.editedIndex === -1
          ? this.$t('setting.messages.new_setting')
          : this.$t('setting.messages.edit_setting')
      },
      getEditedItem () {
        return this.editedItem
      },
      ...get('automation', ['status', 'list', 'message']),
    },
    watch: {
      options: {
        handler (value, oldValue) {
          this.initialize()
        },
        deep: true,
      },
      logsDialog: {
        handler (value) {
          if (!value) this.close()
        },
        immediate: false,
      },

      list (value) {
        this.$set(this, 'automations', value.data)
        this.currentPage = value.currentPage
        this.totalItems = value.totalItems
        this.totalPages = value.totalPages
      },
      dialog (val) {
        val || this.close()
      },
      editDialog (val) {
        val || this.close()
      },
      dialogDelete (val) {
        val || this.closeDelete()
      },
      status (value) {
        this.loading = value === 'loading'
      },

      message (value) {
        if (value === '') return
        this.snackbar = true
        this.result = this.$t(value)
        setTimeout(() => {
          this.$store.dispatch('automation/clearMessage')
          this.snackbar = false
        }, 3000)
      },
      isFilterStatus: {
        immediate: false,
        handler (value, oldValue) {
          const statusIsOn = value === oldValue
          this.initialize('', statusIsOn)
        },
      },
    },
    created () {
      this.editedItem = Object.assign({}, this.defaultItem)
    },
    mounted () {
      this.headers = [
        {
          text: '',
          align: 'start',
          value: 'id',
          sortable: false,
        },
        { text: this.$t('setting.headers.image'), value: 'product.image' },
        {
          text: this.$t('setting.labels.product_name'),
          value: 'product.name',
        },

        { text: this.$t('setting.headers.from'), value: 'fromTime' },
        { text: this.$t('setting.headers.to'), value: 'toTime' },
        { text: this.$t('setting.headers.quantity'), value: 'quantity' },
        { text: this.$t('setting.headers.price'), value: 'price' },
        {
          text: this.$t('setting.headers.status'),
          value: 'status',
          sortable: false,
        },
        {
          text: this.$t('setting.headers.purchaseQuantity'),
          value: 'purchaseQuantity',
        },

        { text: this.$t('setting.headers.created_date'), value: 'createdDate' },
        {
          text: this.$t('setting.headers.action'),
          value: 'actions',
          sortable: false,
        },
      ]
    },
    methods: {
      initialize (search = true, status = true) {
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
          if (!search) {
            tmpOptions.page = 1
            this.options.page = 1
          }
          tmpOptions.search = this.filterAsin
        }
        if (this.isFilterStatus) {
          if (!status) {
            tmpOptions.page = 1
            this.options.page = 1
          }
          tmpOptions.status = this.isFilterStatus
        }
        this.currentOptions = Object.assign({}, tmpOptions)
        this.$store.dispatch('automation/getList', tmpOptions)
      },

      editItem (item) {
        this.assignEditedItem(item)

        this.editDialog = true
      },

      deleteItem (item) {
        this.assignEditedItem(item)
        this.dialogDelete = true
      },
      openItemLog (item) {
        this.assignEditedItem(item)
        this.logsDialog = true
      },
      assignEditedItem (item) {
        this.editedIndex = this.automations.indexOf(item)
        this.editedItem = Object.assign({}, item)
      },
      deleteItemConfirm () {
        this.automations.splice(this.editedIndex, 1)
        this.$store.dispatch('automation/delete', {
          options: this.currentOptions,
          id: this.editedItem.id,
        })
        this.closeDelete()
      },

      close () {
        this.dialog = false
        this.editDialog = false
        this.logsDialog = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem)
          this.editedIndex = -1
        })
      },

      closeDelete () {
        this.dialogDelete = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem)
          this.editedIndex = -1
        })
      },
      changeStatus (item) {
        this.$store.dispatch('automation/changeStatus', {
          id: item.id,
          status: item.status,
          options: this.currentOptions,
        })
      },
      save () {
        if (this.editedIndex > -1) {
          Object.assign(this.automations[this.editedIndex], this.editedItem)
        } else {
          this.automations.push(this.editedItem)
        }
        this.close()
      },
      onShowSnackBar (value, timeout = 3000) {
        if (value === '') return
        this.snackbar = true
        this.result = this.$t(value)
        this.snackbarTimeout = timeout
        setTimeout(() => {
          this.snackbar = false
          this.$store.dispatch('automation/clearMessage')
        }, timeout)
      },
      minuesToTime (val) {
        let minues = val % 60
        let hours = (val - minues) / 60
        if (hours === 24 && minues === 0) hours = 0
        minues = minues < 10 ? '0' + minues : minues
        return hours + ':' + minues
      },
      filter () {
        if (this.status !== 'loading') {
          this.initialize(this.filterAsin, this.isFilterStatus)
        } else this.onShowSnackBar('messages.please_wait', 2000)
      },
      filterSearch (value, oldValue) {
        const isSearch = value === oldValue
        this.initialize(isSearch)
      },
    },
  }
</script>

<style>
td {
  margin-bottom: 100px;
}
</style>
