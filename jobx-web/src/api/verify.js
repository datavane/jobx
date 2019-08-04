import http from '@/utils/request'

export function recent(query) {
  return http.post('/verify/recent', { cronExp: query } )
}
