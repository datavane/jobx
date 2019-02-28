import request from '@/utils/request'
import md5 from 'md5'

export function login(userName, password) {
  return request.post('/passport/login',{
    'userName': userName,
    'password': md5(password)
  })
}

export function getInfo() {
  return request.post('/user/info')
}

export function logout() {
  return request.post('/passport/logout')
}
