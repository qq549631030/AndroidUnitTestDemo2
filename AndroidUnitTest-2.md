# Android单元测试（二） - dagger2

前一篇 View层为了把ILoginPresenter注入到LoginActivity中我们使用了一个PresenterManager来生成ILoginPresenter,  同时要生成LoginPresenter又要先生成ILoginModel，于是又用一个ModelManager来生成ILoginModel,这样一层又一层很复杂也很容易出错，要是某个类要依赖很多基它类就更复杂，我们可以使用依赖注入框架[dagger2](https://github.com/google/dagger)来简化实现。

```groovy
apply plugin: 'kotlin-kapt'
dependencies {
    implementation 'com.google.dagger:dagger:2.28.1'
    kapt 'com.google.dagger:dagger-compiler:2.28.1'
}
```

#### LoginModel 

构造方法加上@Inject注解，目的是为了让dagger知道怎么实例化LoginModel 

```kotlin
class LoginModel @Inject constructor() : ILoginModel {
    override fun login(userName: String, password: String, callback: Callback) {

        if (userName == "huangx" && password == "123456") {
            callback.onSuccess("登录成功")
        } else {
            callback.onError(Exception("用户名或密码错误"))
        }
    }
}
```

#### LoginPresenter

构造方法加上@Inject注解让dagger知道怎么实例化LoginPresenter，loginView不从构造方法里传入，改用atachView方法传入，因为最终的ILoginView是Activity Dagger 无法实例化

```kotlin
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
```

编写LoginModule告诉dagger怎么产生ILoginModel与ILoginPresenter

```kotlin
@Module
class LoginModule {
    @Provides
    internal fun provideLoginModel(loginModel: LoginModel): ILoginModel {
        return loginModel
    }

    @Provides
    internal fun provideLoginPresenter(loginPresenter: LoginPresenter): ILoginPresenter {
        return loginPresenter
    }
}
```

Dagger生成实例有两种，一种是@Provides注解的方法另一种就是@Inject注解的构造函数。

如要生成一个ILoginModel，会找到@Provides注解的返回值类型是ILoginModel的provideLoginModel方法，它有一个参数loginModel，说明要生成ILoginModel前提是要先生成LoginModel，因为没有找到@Provides注解的方法生成LoginModel，故去找LoginModel的构造方法，发现有被@Inject注解，而且构造方法不需要参数，于是就用LoginModel无参构造方法直接生成LoginModel。

同理LoginPresenter也是这样生成，不过LoginPresenter的构造方法有参数ILoginModel，要构造LoginPresenter请提是构造出前面一步的ILoginModel。

#### LoginActivity

```kotlin
class LoginActivity : AppCompatActivity(), ILoginView {

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
```

到此dagger接入完成了

下面看单元测试，LoginModel和LoginPresenter的单元测试跟前面一样没有变化，主要是LoginActivity的变化

```kotlin
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class LoginActivityTest {

    @Test
    fun loginSuccess() {
        //mock LoginModel的构造函数就行了
        mockkConstructor(LoginModel::class)
        every { anyConstructed<LoginModel>().login(any(), any(), any()) } answers {
            //获取方法的第三个参数Callback,调用Callback的onSuccess方法
            arg<Callback>(2).onSuccess("登录成功")
        }
        //启动LoginActivity
        val activityController = Robolectric.buildActivity(LoginActivity::class.java)
        activityController.setup()
        Espresso.onView(ViewMatchers.withId(R.id.et_username))
            .perform(ViewActions.typeText("huangx"))
        Espresso.onView(ViewMatchers.withId(R.id.et_password))
            .perform(ViewActions.typeText("123456"))
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click())
        verify { anyConstructed<LoginModel>().login("huangx", "123456", any()) }
        assertEquals("登录成功", ShadowToast.getTextOfLatestToast())
        activityController.destroy()
        unmockkConstructor(LoginModel::class)
    }
}
```



