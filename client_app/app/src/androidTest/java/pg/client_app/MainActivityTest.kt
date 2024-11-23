package pg.client_app

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun testMainActivity() {
        // Arrange
        val expectedResponse = "Base64EncodedImageString"

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { mainActivity ->
                // Inject a mock sender
                mainActivity.requestSender = { leftTopX, leftTopY, rightBottomX, rightBottomY, onResponse ->
                    // Assert the inputs
                    assertEquals(0, leftTopX)
                    assertEquals(0, leftTopY)
                    assertEquals(100, rightBottomX)
                    assertEquals(100, rightBottomY)
                    // Simulate the server response
                    onResponse(expectedResponse)
                }

                // Act
                var actualResponse: String? = null
                mainActivity.sendRequest(0, 0, 100, 100) {
                    actualResponse = it
                }

                // Assert
                assertEquals(expectedResponse, actualResponse)
            }
        }
    }
}
