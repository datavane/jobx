import api from './index'
import http from '@/utils/request'

export function login (parameter) {
  return http.post(api.Login, parameter)
}

export function getSmsCaptcha (parameter) {
  return http.post(api.SendSms, parameter)
}

export function getInfo () {
  return http.post('/user/info')
}

export function logout () {
  return http.post('/passport/logout')
}
