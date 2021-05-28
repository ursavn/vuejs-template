<template>
  <v-navigation-drawer
    id="default-drawer"
    v-model="drawer"
    :dark="dark"
    :right="$vuetify.rtl"
    :src="drawerImage ? image : ''"
    :mini-variant.sync="mini"
    mini-variant-width="80"
    app
    width="260"
  >
    <template
      v-if="drawerImage"
      #img="props"
    >
      <v-img
        :key="image"
        :gradient="gradient"
        v-bind="props"
      />
    </template>

    <div class="px-2">
      <default-drawer-header />

      <v-divider class="mx-3 mb-2" />

      <default-list :items="listItems" />
    </div>
    <div class="pt-12" />
  </v-navigation-drawer>
</template>

<script>
// Utilities
  import { get, sync } from 'vuex-pathify'

  export default {
    name: 'DefaultDrawer',

    components: {
      DefaultDrawerHeader: () =>
      import(
        /* webpackChunkName: "default-drawer-header" */
        './widgets/DrawerHeader'
      ),
      DefaultList: () =>
      import(
        /* webpackChunkName: "default-list" */
        './List'
      ),
    },
    data () {
      return {
        listItems: [],
      }
    },
    computed: {
      ...get('user', ['user_data', 'dark', 'gradient', 'image']),
      ...get('app', ['version']),
      ...sync('app', ['drawer', 'items', 'drawerImage', 'mini']),
    },
    created () {
      this.listItems = [
        {
          title: this.$t('messages.dashboard'),
          icon: 'mdi-view-dashboard',
          to: '/',
        },
        {
          title: this.$t('messages.amazon_account_title'),
          icon: 'mdi-amazon',
          to: '/account-list/',
        },
        {
          title: this.$t('user.list_user.title'),
          icon: 'mdi-account',
          to: '/user-list/',
        },
        {
          title: this.$t('setting.headers.title'),
          icon: 'mdi-auto-upload',
          to: '/automation-list/',
        },
        {
          title: this.$t('order.title'),
          icon: 'mdi-receipt',
          to: '/order-list/',
        },
        {
          title: this.$t('line.title'),
          icon: 'mdi-apps',
          to: '/line/',
        },
      ]
    },
  }
</script>

<style lang="sass">
#default-drawer
  .v-list-item
    margin-bottom: 8px

  .v-list-item::before,
  .v-list-item::after
    display: none

  .v-list-group__header__prepend-icon,
  .v-list-item__icon
    margin-top: 12px
    margin-bottom: 12px
    margin-left: 4px

  &.v-navigation-drawer--mini-variant
    .v-list-item
      justify-content: flex-start !important
</style>
