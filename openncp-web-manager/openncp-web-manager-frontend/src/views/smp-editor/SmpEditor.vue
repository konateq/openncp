<template>
  <v-container>
    <span class="display-2">SMP Editor</span>
    <v-tabs v-model="selectedTab" optional>
      <v-tab :to="{ name: 'SmpGenerate' }">Generate</v-tab>
      <v-tab :to="{ name: 'SmpSign' }">Sign </v-tab>
      <v-tab :to="{ name: 'SmpUpload' }">Upload File to SMP Server</v-tab>
      <v-tab :to="{ name: 'SmpDelete' }">List SMP services</v-tab>
      <v-tab :to="{ name: 'SmpSynchronize' }">Synchronize Search Mask</v-tab>
    </v-tabs>
    <router-view />
  </v-container>
</template>

<script>
import axios from 'axios'
import _ from 'lodash'

export default {
  name: 'SmpEditor',
  data () {
    return {
      loading: false,
      selectedTab: -1,
      message: null
    }
  },
  methods: {},
  mounted () {
    axios
      .get(process.env.VUE_APP_SERVER_URL + '/api/properties')
      .then((response) => {
        const country = _.find(response.data, (item) => {
          return item.key === 'COUNTRY_CODE'
        }).value
        this.$store.commit('country', country)
        this.properties = response.data
        this.loading = false
      })
      .catch((err) => {
        this.error(
          'An error occurs. The operations are not completed! <br/>' + err.response.data.message
        )
      })
  }
}
</script>

<style scoped></style>
