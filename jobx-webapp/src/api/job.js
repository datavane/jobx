import request from '@/utils/request'

export function view(params) {
  return request.post('/job/view',params)
}

export function addJob(params) {
  return request.post('/job/addJob',params)
}

export function addDependency(params) {
  return request.post('/job/addDependency',params)
}

export function getJob(params) {
  return request.post('/job/getJob',params)
}
