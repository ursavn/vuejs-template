<template>
  <material-card
    color="primary"
    icon="mdi-note-text"
  >
    <v-data-table
      max-width="80%"
      :headers="headers"
      :items="currentSettingLog"
      :options.sync="options"
      :server-items-length="totalItems"
      :loading="loading"
      class="elevation-1"
      :loading-text="$t('messages.loading_item')"
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-container class="mt-5">
            <v-row>
              <v-col cols="3">
                <v-toolbar-title class="float-left">
                  {{
                    $t('messages.log_title', {
                      asin: currentSetting.product.asin,
                    })
                  }}
                </v-toolbar-title>
                <v-divider
                  class="mx-4"
                  inset
                  vertical
                />
              </v-col>
            </v-row>
          </v-container>
        </v-toolbar>
      </template>
      <template v-slot:item="props">
        <tr>
          <td width="5%">
            {{ props.item.id }}
          </td>
          <td
            class="text-left"
            width="30%"
          >
            {{ props.item.content }}
          </td>
          <td
            class="text-left"
            width="5%"
          >
            <span>{{ props.item.type }}</span>
          </td>
          <td
            class="text-left"
            width="15%"
          >
            {{ props.item.createdDate | formatDate }}
          </td>
        </tr>
      </template>
      <template slot="no-data">
        {{ $t('messages.list_empty') }}
      </template>
      <template v-slot:footer.page-text="items">
        {{ items.pageStart }} - {{ items.pageStop }}
        {{ $t('v-table.headers.page-text') }}
        {{ items.itemsLength }}
      </template>
    </v-data-table>

    <v-snackbar
      v-model="snackBar"
      :timeout="snackBarTimeout"
    >
      {{ result }}

      <template v-slot:action="{ attrs }">
        <v-btn
          color="pink"
          text
          v-bind="attrs"
          @click="snackBar = false"
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
    name: 'SettingPopupLog',
    props: {
      currentSetting: Object,
      close: Function,
    },

    data: () => {
      return {
        result: '',
        headers: [],
        currentSettingLog: [],
        currentItem: {},
        snackBarTimeout: 6000,
        asinError: false,
        defaultRating: 4.0,
        totalItems: 0,
        loading: false,
        options: {},
        snackBar: false,
      }
    },
    computed: {
      ...get('automation', ['message', 'logStatus', 'settingLog']),
    },
    watch: {
      logStatus (value) {
        this.loading = value === 'loading'
      },
      settingLog (value) {
        this.$set(this, 'currentSettingLog', value.data)
        this.currentPage = value.currentPage
        this.totalItems = value.totalItems
        this.totalPages = value.totalPages
      },
      options () {
        if (this.currentSetting.id) this.initialize(this.currentSetting.id)
      },
      currentSetting: {
        handler (value) {
          if (value.id) this.initialize(value.id)
        },
      },
    },
    mounted () {
    // this.initialize(this.currentItem.id)
    },
    created () {
      this.headers = [
        {
          text: this.$t('setting.headers.id'),
          align: 'start',
          value: 'id',
          sortable: false,
        },
        {
          text: this.$t('setting.headers.content'),
          value: 'content',
          sortable: false,
        },
        {
          text: this.$t('setting.headers.type'),
          value: 'type',
        },
        {
          text: this.$t('setting.headers.created_date'),
          value: 'createdDate',
          sortable: false,
        },
      ]
    },
    methods: {
      initialize (id) {
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

        this.$store.dispatch('automation/getSettingLog', {
          options: tmpOptions,
          id: id,
        })
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
