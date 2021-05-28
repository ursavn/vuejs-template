import Vue from 'vue'
import VueI18n from 'vue-i18n'

Vue.use(VueI18n)

function loadLocaleMessages () {
  const locales = require.context('./', true, /[A-Za-z0-9-_,\s]+\.json$/i)
  const messages = {}
  locales.keys().forEach(key => {
    const matched = key.match(/([A-Za-z0-9-_]+)\./i)
    if (matched && matched.length > 1) {
      const locale = matched[1]
      messages[locale] = locales(key)
    }
  })
  return messages
}
const numberFormats = {
  ja: {
    currency: {
      style: 'currency',
      currency: 'JPY',
      currencyDisplay: 'symbol',
    },
    currencyNoCents: {
      style: 'currency',
      currency: 'JPY',
      currencyDisplay: 'symbol',
      minimumFractionDigits: 0, // set fraction digits to 0 to remove cents
      maximumFractionDigits: 0,
    },
  },
}
export default new VueI18n({
  numberFormats,
  locale: 'ja',
  fallbackLocale: 'ja',
  messages: loadLocaleMessages(),
})
