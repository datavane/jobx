import * as types from './mutation-types'
import storage from '@/utils/storage.js'
import constant from '@/utils/constant.js'

const mutations = {
  [types.LOGIN](state, user) {
    state.user = user
    storage.set(constant.keys.user, user)
  },
  [types.LOGOUT](state) {
    state.user = null
    storage.remove(constant.keys.user)
    storage.remove(constant.keys.xsrf)
  },
  [types.TOGGLE_LOADING](state, flag) {
    state.loading = flag
  }
}

export default mutations
