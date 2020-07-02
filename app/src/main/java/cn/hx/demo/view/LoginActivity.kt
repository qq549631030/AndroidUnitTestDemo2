package cn.hx.demo.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.hx.demo.R
import cn.hx.demo.di.DaggerLoginComponent
import cn.hx.demo.di.LoginModule
import cn.hx.demo.presenter.ILoginPresenter
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), ILoginView {

    //通过dagger注入
    @Inject
    lateinit var loginPresenter: ILoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //使用前注入loginPresenter
        DaggerLoginComponent.builder()
            .loginModule(LoginModule())
            .build()
            .inject(this)
        //把ILoginView传入loginPresenter
        loginPresenter.attachView(this)
        btn_login.setOnClickListener {
            loginPresenter.login()
        }
    }

    override fun getUserName(): String {
        return et_username.text.toString()
    }

    override fun getPassword(): String {
        return et_password.text.toString()
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        loginPresenter.detachView()
        super.onDestroy()
    }
}