package cn.hx.demo.view

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.hx.demo.R
import cn.hx.demo.model.Callback
import cn.hx.demo.model.LoginModel
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import io.mockk.verify
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

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