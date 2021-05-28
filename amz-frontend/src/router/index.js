// Imports
import Vue from 'vue'
import Router from 'vue-router'
import { layout, route } from '@/util/routes'
import store from '@/store'

Vue.use(Router)

const router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  scrollBehavior: (to, from, savedPosition) => {
    if (to.hash) return { selector: to.hash }
    if (savedPosition) return savedPosition

    return { x: 0, y: 0 }
  },
  routes: [
    layout('Default', [
      route('Dashboard', null, '', true),

      // Pages
      route('AutomationList', null, 'automation-list', true),
      route('UserProfile', null, 'profile', true),
      route('UserChangePass', null, 'change-pass', true),
      route('AddUser', null, 'add-user', true),
      route('UserList', null, 'user-list', true),
      route('AccountList', null, 'account-list', true),
      route('AmazonAccountEdit', null, 'account-edit/:id', true),
      route('EditUser', null, 'edit-user/:id', true),
      route('OrderList', null, 'order-list/', true),
      route('LineDetail', null, 'line/', true),
      // Components
      route('Notifications', null, 'notifications', true),
      route('Icons', null, 'components/icons', true),
      route('Typography', null, 'components/typography', true),
      // Tables
      route('Regular Tables', null, 'tables/regular', true),

      route('LineFallback', null, 'callback', true),
    ]),
    layout('Special', [
      route('Login', null, 'login', false),
      route('UserForgotPassword', null, 'forgot-pass', false),
      route('UserResetPassword', null, 'forgot-password/confirm', false),
      route('Error', null, '*', false),
    ]),
  ],
})

router.beforeEach((to, from, next) => {
  if (to.name === 'Login') {
    if (store.state.user.logged) {
      next({ name: 'Dashboard' })
    } else {
      next()
    }
  } else if (to.matched.some(record => record.meta.requireAuthentication)) {
    if (!store.state.user.logged) {
      next({
        name: 'Login',
        query: { redirect: to.fullPath },
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
