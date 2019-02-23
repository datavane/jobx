import request from '@/utils/request'

export function view(params) {
  return request.post('/job/view',params)
}

export function addJob(params) {
  return request.post('/job/addJob',params)
}

export function addNode(params) {
  return request.post('/job/addNode',params)
}

export function getJob(params) {
  return request.post('/job/getJob',params)
}
