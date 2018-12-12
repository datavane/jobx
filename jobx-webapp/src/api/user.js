import request from '@/utils/request'

export function execUser() {
  return request.post('/user/execUser')
}
