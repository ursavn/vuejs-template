<template>
  <v-container>
    <v-row align="center">
      <v-spacer class="hidden-sm-and-down" />

      <v-col
        cols="12"
        md="auto"
      >
        <div class="text-body-1 font-weight-light pt-6 pt-md-0 text-center">
          &copy; {{ (new Date()).getFullYear() }}, {{ $t('messages.all_right_reserve') }}.
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
  export default {
    name: 'Links',
  }
</script>

<style lang="sass" scoped>
  a
    color: inherit !important
</style>
