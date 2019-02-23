import request from '@/utils/request'

export function getExecUser() {
  return request.post('/user/execUser')
}
