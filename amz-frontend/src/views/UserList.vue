<template>
  <material-card
    color="primary"
    icon="mdi-account"
  >
    <v-data-table
      :headers="headers"
      :items="users"
      :options.sync="options"
      :loading-text="$t('messages.loading_item')"
      :loading="loading"
      :server-items-length="totalItems"
      class="elevation-1"
      :footer-props="{
        itemsPerPageOptions: [5, 10, 15, 100],
        showFirstLastPage: true,
        'items-per-page-text': $t('v-table.headers.items-per-page'),
      }"
    >
      <template v-slot:item="props">
        <tr>
          <td>
            <b>{{ props.item.id }}</b>
          </td>
          <td>
            <span>{{ props.item.email }}</span>
          </td>
          <td>
            <span>{{ props.item.fullName }}</span>
          </td>

          <td width="20%">
            {{ props.item.createdDate | formatDate }}
          </td>
          <td width="20%">
            {{ props.item.modifiedDate | formatDate }}
          </td>
          <td>
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
            <!-- <v-icon
                    small
                    class="mr-2"
                    @click="editItem(item)"
                  >
                    mdi-pencil
                  </v-icon> -->
          </td>
        </tr>
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
                  {{ $t('user.list_user.title') }}
                </v-toolbar-title>
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
                  @keyup.enter="filterSearch"
                  @click:prepend="filterSearch"
                />
              </v-col>
              <v-col>
                <v-spacer />
                <template>
                  <v-btn
                    v-show="user_data.role === 'ADMIN'"
                    color="primary"
                    dark
                    class="mb-2 float-right"
                    @click="$router.push('/add-user')"
                  >
                    {{ $t('messages.new_account') }}
                  </v-btn>
                </template>
              </v-col>
            </v-row>
          </v-container>
        </v-toolbar>
        <v-dialog
          v-model="deleteDialog"
          max-width="500px"
        >
          <v-card>
            <v-card-title>
              <span
                v-html="
                  $t('messages.do_you_want_to_delete_account', {
                    account: editedItem.email,
                  })
                "
              />
            </v-card-title>
            <v-card-actions>
              <v-spacer />
              <v-btn
                color="green darken-1"
                text
                @click="closeDialog"
              >
                {{ $t('messages.no') }}
              </v-btn>
              <v-btn
                color="red darken-1"
                text
                @click="processDeleteItem"
              >
                {{ $t('messages.yes') }}
              </v-btn>
              <v-spacer />
            </v-card-actions>
          </v-card>
        </v-dialog>
      </template>

      <template v-slot:no-data>
        {{ $t('messages.list_empty') }}
        <v-btn
          color="primary"
          @click="initialize"
        >
          {{ $t('messages.reload') }}
        </v-btn>
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
  export default {
    data: () => ({
      options: {},
      dialog: false,
      deleteDialog: false,
      loading: false,
      totalRecord: null,
      headers: [],
      editedIndex: -1,
      editedItem: {},
      totalItems: 0,
      currentPage: 0,
      users: [],
      snackbar: false,
      result: '',
      filterName: '',
      currentOptions: {},
    }),
    computed: {
      ...get('user', ['user_data', 'status', 'userList', 'message']),
    },
    watch: {
      message (value) {
        if (value === '') return
        this.snackbar = true
        this.result = this.$t(value)
        setTimeout(() => {
          this.$store.dispatch('user/clearMessage')
          this.snackbar = false
        }, 3000)
      },
      userList (value) {
        this.users = [...value.data]
        this.currentPage = value.currentPage
        this.totalItems = value.totalItems
        this.totalPages = value.totalPages
      },
      options: {
        handler () {
          this.initialize()
        },

        deep: true,
      },
      status () {
        this.loading = this.status === 'loading'
      },
    },
    created () {
      this.headers = [
        {
          text: 'ID',
          align: 'start',
          value: 'id',
        },
        { text: this.$t('messages.email'), value: 'email' },
        { text: this.$t('messages.full_name'), value: 'fullName' },
        { text: this.$t('messages.created_date'), value: 'createdDate' },
        { text: this.$t('messages.updated_date'), value: 'modifiedDate' },
        {
          text: this.$t('messages.actions'),
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

        if (!isSearch) {
          tmpOptions.search = this.filterName
          tmpOptions.page = 1
          this.$options.page = 1
        }

        this.currentOptions = Object.assign({}, tmpOptions)
        this.$store.dispatch('user/getUserList', tmpOptions)
      },
      editItem (item) {
        this.editedIndex = this.users.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.$router.push({ path: '/edit-user/' + this.editedItem.id })
      },
      deleteItem (item) {
        this.deleteDialog = true
        this.editedIndex = this.users.indexOf(item)
        this.editedItem = Object.assign({}, item)
        if (this.editedItem.role === 'ADMIN') {
          this.deleteDialog = false
          this.$store.dispatch('user/setMessage', {
            message: 'messages.cant_delete_admin_account',
          })
        }
        if (
          this.editedItem.id === this.user_data.id ||
          this.user_data.role !== 'ADMIN'
        ) {
          this.deleteDialog = false
          this.$store.dispatch('user/setMessage', {
            message: 'messages.unauthorize',
          })
          if (this.editedItem.role === 'ADMIN') {
            this.$store.dispatch('user/setMessage', {
              message: 'messages.cant_delete_admin_account',
            })
          }
        }
      },
      processDeleteItem () {
        const payload = {
          id: this.editedItem.id,
          options: this.currentOptions,
        }
        this.$store.dispatch('user/delete', payload)
        this.closeDialog()
      },
      closeDialog () {
        this.deleteDialog = false
        this.editedItem = {}
        this.editedIndex = -1
      },
      onShowSnackBar (value, timeout = 3000) {
        if (value === '') return
        this.snackbar = true
        this.result = this.$t(value)
        this.snackbarTimeout = timeout
        setTimeout(() => {
          this.snackbar = false
          this.$store.dispatch('user/clearMessage')
        }, timeout)
      },
      filter () {
        if (this.status !== 'loading') {
          this.initialize(this.filterName)
        } else this.onShowSnackBar('messages.please_wait', 2000)
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

<style scoped></style>
