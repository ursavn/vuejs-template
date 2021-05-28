<template>
  <v-container
    id="user-profile-view"
    fluid
    tag="section"
  >
    <v-row justify="center">
      <v-col
        cols="12"
        md="8"
      >
        <material-card
          color="primary"
          icon="mdi-account-outline"
        >
          <template #title>
            Edit Profile
          </template>

          <v-form>
            <v-container class="py-0">
              <v-row>
                <v-col
                  cols="3"
                  md="2"
                >
                  <p>
                    <b><span>Email:</span></b>
                  </p>
                </v-col>
                <v-col
                  cols="12"
                  md="4"
                >
                  <p>{{ user.email }}</p>
                </v-col>
              </v-row>
              <v-row>
                <v-col
                  cols="3"
                  md="2"
                >
                  <p>
                    <b><span>Full name:</span></b>
                  </p>
                </v-col>
                <v-col
                  cols="12"
                  md="6"
                >
                  <p>{{ user.fullname }}</p>
                </v-col>
              </v-row>
              <v-row>
                <v-col
                  cols="3"
                  md="2"
                >
                  <p>
                    <b><span>Created Date:</span></b>
                  </p>
                </v-col>
                <v-col
                  cols="12"
                  md="6"
                >
                  <p>{{ user.createdDate }}</p>
                </v-col>
              </v-row>
              <v-row>
                <v-col
                  cols="3"
                  md="2"
                >
                  <p>
                    <b><span>Last Updated Date:</span></b>
                  </p>
                </v-col>
                <v-col
                  cols="12"
                  md="6"
                >
                  <p>{{ user.lastUpdatedDate }}</p>
                </v-col>
              </v-row>
              <v-row>
                <v-col
                  md="4"
                  cols="4"
                >
                  <v-checkbox
                    label="STATUS"
                    :value="user.status"
                    :color="user.status === 'ACTIVE' ? 'green' : 'red'"
                  />
                </v-col>
              </v-row>
            </v-container>
          </v-form>
        </material-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
  const fakeuser = {
    createdDate: '03/17/2021 07:29:44',
    lastUpdatedDate: '03/17/2021 07:29:44',
    id: 1,
    email: 'admin@gmail.com',
    fullname: 'Administrator',
    status: 'ACTIVE',
  }
  export default {
    name: 'UserProfileView',
    data () {
      return {
        user: fakeuser,
      }
    },
    created () {
      this.user = this.$store.dispatch('user/getUser', {
        id: this.$route.params.id,
      })
    },
    methods: {
      goToChangePass () {
        this.$router.push({ name: 'UserChangePass' })
      },
    },
  }
</script>
