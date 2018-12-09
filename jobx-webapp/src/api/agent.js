import request from '@/utils/request'

export function allAgent() {
  return request.post('/agent/all')
}

