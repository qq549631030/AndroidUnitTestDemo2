package cn.hx.demo.model

import java.lang.Exception
import javax.inject.Inject

class LoginModel @Inject constructor() : ILoginModel {
    override fun login(userName: String, password: String, callback: Callback) {

        if (userName == "huangx" && password == "123456") {
            callback.onSuccess("登录成功")
        } else {
            callback.onError(Exception("用户名或密码错误"))
        }
    }
}