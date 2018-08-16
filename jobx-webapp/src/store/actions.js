import * as types from './mutation-types'

export const login = function ({commit}, {user}) {
  commit(types.LOGIN, user)
}
export const logout = function ({commit}) {
  commit(types.LOGOUT)
}

export const toggleLoading = function ({commit}, flag) {
  commit(types.TOGGLE_LOADING, flag)
}

