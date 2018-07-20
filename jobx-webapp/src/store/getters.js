import storage from '@/utils/storage.js'
import constant from '@/utils/constant.js'

export const user = (state) => state.user || storage.get(constant.keys.user)

export const loading = state => state.loading
