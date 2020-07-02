package cn.hx.demo.di

import cn.hx.demo.view.LoginActivity
import dagger.Component

@Component(modules = [LoginModule::class])
interface LoginComponent {

    fun inject(loginActivity: LoginActivity)
}