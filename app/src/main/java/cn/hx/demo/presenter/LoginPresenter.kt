package cn.hx.demo.presenter

import cn.hx.demo.model.Callback
import cn.hx.demo.model.ILoginModel
import cn.hx.demo.view.ILoginView
import java.lang.Exception
import javax.inject.Inject

class LoginPresenter @Inject
constructor(private val loginModel: ILoginModel) :
    ILoginPresenter {

    var loginView: ILoginView? = null

    override fun attachView(view: ILoginView) {
        loginView = view
    }


    override fun login() {
        loginView?.let {
            loginModel.login(
                it.getUserName(),
                it.getPassword(),
                object : Callback {
                    override fun onSuccess(result: String) {
                        loginView?.showToast(result)
                    }

                    override fun onError(e: Exception) {
                        loginView?.showToast(e.message.toString())
                    }
                })
        }
    }

    override fun detachView() {
        loginView = null
    }
}