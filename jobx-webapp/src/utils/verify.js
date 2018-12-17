/**
 * Created by jiachenpan on 16/11/18.
 */

export default {

  isValidUsername(str) {
    const valid_map = ['admin', 'editor']
    return valid_map.indexOf(str.trim()) >= 0
  },

  /* 合法uri*/
  isURL(textval) {
    const urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
    return urlregex.test(textval)
  },

  /* 合法钉钉机器人URL*/
  isDingTaskURL(textval) {
    const urlregex = /^https\:\/\/oapi\.dingtalk\.com\/robot\/send\?access_token=[a-z0-9]{64}$/
    return urlregex.test(textval)
  },

  /* 邮箱*/
  isEmail(textval) {
    const urlregex = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/
    return urlregex.test(textval)
  },

  /* 手机号*/
  isPhone(textval) {
    const urlregex = /^1(3|4|5|7|8)\d{9}$/
    return urlregex.test(textval)
  },

  /* 小写字母*/
  isLowerCase(str) {
    const reg = /^[a-z]+$/
    return reg.test(str)
  },

  /* 大写字母*/
  isUpperCase(str) {
    const reg = /^[A-Z]+$/
    return reg.test(str)
  },

  /* 大小写字母*/
  isAlphabets(str) {
    const reg = /^[A-Za-z]+$/
    return reg.test(str)
  },

  isPositiveNum(str){
    var re = /^[0-9]+$/ ;
    return re.test(str)
  }

}

