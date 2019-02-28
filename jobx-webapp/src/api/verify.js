import request from '@/utils/request'

export function recent(query) {
  return request.post('/verify/recent',{
    cronExp: query
  })
}

