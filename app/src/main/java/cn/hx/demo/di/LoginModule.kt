package cn.hx.demo.di

import cn.hx.demo.model.ILoginModel
import cn.hx.demo.model.LoginModel
import cn.hx.demo.presenter.ILoginPresenter
import cn.hx.demo.presenter.LoginPresenter
import dagger.Module
import dagger.Provides

@Module
class LoginModule {
    @Provides
    internal fun provideLoginModel(): ILoginModel {
        return LoginModel()
    }

    @Provides
    internal fun provideLoginPresenter(loginModel: LoginModel): ILoginPresenter {
        return LoginPresenter(loginModel)
    }
}