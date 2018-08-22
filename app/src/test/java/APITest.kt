package com.tsystems.logiweb.manhwa.smoker

import com.tsystems.logiweb.manhwa.smoker.backend.UserLoginAsync
import com.tsystems.logiweb.manhwa.smoker.backend.VerifyTokenAsync
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class APITest: StringSpec({

    "Received Token Should Be Valid" {
        val token = UserLoginAsync.userLogin("akachuri", "akachuri")!!
        val tokenValid = VerifyTokenAsync.verifyToken(token)
        tokenValid shouldBe true
    }

})
