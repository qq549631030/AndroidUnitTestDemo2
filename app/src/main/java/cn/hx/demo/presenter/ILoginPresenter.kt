package cn.hx.demo.presenter

import cn.hx.demo.view.ILoginView

interface ILoginPresenter {
    fun attachView(view: ILoginView)
    fun login()
    fun detachView()
}