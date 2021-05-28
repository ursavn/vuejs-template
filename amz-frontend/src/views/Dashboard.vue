<template>
  <v-container
    id="dashboard-view"
    fluid
    tag="section"
  >
    <v-row>
      <v-col
        cols="12"
        md="6"
        lg="3"
      >
        <material-stat-card
          v-bind="stats[0]"
          :value="users + ''"
        >
          <template #actions>
            <v-icon
              class="mr-2"
              small
            />
          </template>
        </material-stat-card>
      </v-col>
      <v-col
        cols="12"
        md="6"
        lg="3"
      >
        <material-stat-card
          v-bind="stats[1]"
          :value="automation + ''"
        >
          <template #actions>
            <v-icon
              class="mr-2"
              small
            />
          </template>
        </material-stat-card>
      </v-col>
      <v-col
        cols="12"
        md="6"
        lg="3"
      >
        <material-stat-card
          v-bind="stats[2]"
          :value="amazonAccount + ''"
        >
          <template #actions>
            <v-icon
              class="mr-2"
              small
            />
          </template>
        </material-stat-card>
      </v-col>
    </v-row>
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
  </v-container>
</template>

<script>
// Utilities
  import { get } from 'vuex-pathify'
  export default {
    name: 'DashboardView',

    data: () => ({
      stats: [],
      currentActiveAccounts: 0,
      tabs: 0,
      result: '',
      snackbar: false,
    }),
    computed: {
      ...get('dashboard', [
        'automation',
        'users',
        'amazonAccount',
        'message',
        'status',
      ]),

      totalSales () {
        return this.sales.reduce((acc, val) => acc + val.salesInM, 0)
      },
    },
    watch: {
      message (value) {
        this.result = this.$t(value)
        this.snackBar = true
        setTimeout(() => {
          this.snackBar = false
        }, 3000)
        this.$store.dispatch('dashboard/clearMessage')
      },
    },
    created () {
      this.$store.dispatch('dashboard/init')
      this.stats = [
        {
          name: 'current_active',
          actionIcon: 'mdi-alert',
          actionText: 'Get More Space...',
          color: '#FD9A13',
          icon: 'mdi-account',
          title: this.$t('dashboard.headers.current_active'),
        },
        {
          name: 'current_automation',
          actionIcon: 'mdi-alert',
          actionText: 'Get More Space...',
          color: '#60f542',
          icon: 'mdi-auto-upload',
          title: this.$t('dashboard.headers.current_automation'),
        },
        {
          name: 'current_amazon_account',
          actionIcon: 'mdi-alert',
          actionText: 'Get More Space...',
          color: '#f542c5',
          icon: 'mdi-amazon',
          title: this.$t('dashboard.headers.current_amazon_account'),
        },
      ]
    },
  }
</script>
