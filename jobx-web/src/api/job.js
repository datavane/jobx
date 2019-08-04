import api from './index'
import http from '@/utils/request'

export function jobList (params) {
  return http.post(api.Job.JobList, params)
}

export function addJob (params) {
  return http.post(api.Job.JobAdd, params)
}

export function addNode (params) {
  return http.post(api.Job.AddNode, params)
}

export function getJob (params) {
  return http.post(api.Job.GetJob, params)
}
