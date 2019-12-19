/*
 * Copyright @2019 by Alexzander Purwoko Widiantoro <purwoko908@gmail.com>
 * This prototype is used for testing, and educating about APIs
 * @author APWDevs
 *
 * Licensed under GNU GPLv3
 *
 * This module is provided by "AS IS" and if you want to take
 * a copy or modifying this module, you must include this @author
 * Thanks! Happy Coding!
 */

package id.apwdevs.andropilkasis

import id.apwdevs.andropilkasis.module.ServerProcessing
import id.apwdevs.andropilkasis.module.migration.UserData
import id.apwdevs.andropilkasis.module.serverResponse.Dashboard
import id.apwdevs.andropilkasis.params.PublicConfig
import id.apwdevs.andropilkasis.plugin.GetServerData
import id.apwdevs.andropilkasis.plugin.encryptMD5
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.net.HttpURLConnection
import java.net.URL

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    //@Mock
    //private lateinit var serverModule: ServerModule

    @Mock
    private lateinit var serverProcessing: ServerProcessing

    private lateinit var dashboard: Dashboard
    val postParams = hashMapOf(
        PublicConfig.SharedKey.KEY_USERNAME to "Brian",
        PublicConfig.SharedKey.KEY_PASSWORD to "vfre4t342",
        "userMode" to UserData.UserMode.ADMIN_TPS.id.toString()
    )

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
        `when`(
            serverProcessing.sendPostRequest(
                "${PublicConfig.ServerConfig.DEF_BASE_DIR}/dashboard.php",
                postParams
            )
        )
            .thenReturn(
                GetServerData.sendPostRequest(
                    "http://192.168.1.100/${PublicConfig.ServerConfig.DEF_BASE_DIR}/dashboard.php",
                    postParams,
                    10000
                )
            )

        dashboard = Dashboard(serverProcessing)
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        val h = encryptMD5("pilwasisadmin21")
        print(h)

        val httpResponse = URL("http://192.168.100.1").openConnection() as HttpURLConnection
        httpResponse.connectTimeout = 10000
        httpResponse.connect()
        val response = httpResponse.responseCode


        val data = arrayOf(
            "XII TKJ 1",
            "XII TKJ 2",
            "XII TKJ 1",
            "XII TKJ 4",
            "XII TKJ 4",
            "XII TKJ 4",
            "XII TKJ 1",
            "XII TKJ 2",
            "XII TKJ 1",
            "XII TKJ 3"
        )


    }


}
