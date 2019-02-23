import request from '@/utils/request'

export function getAgent() {
  return request.post('/agent/all')
}

