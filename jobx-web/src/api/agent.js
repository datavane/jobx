import http from '@/utils/request'

export function getAgent () {
  return http.post('/agent/all')
}
