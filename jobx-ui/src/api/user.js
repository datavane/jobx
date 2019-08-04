import api from './index'
import http from '@/utils/request'

export function getExecUser () {
  return http.post(api.User.ExecUser)
}
