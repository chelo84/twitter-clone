package com.example.oauthdemo.service

import com.example.oauthdemo.model.dto.UserDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest
import uk.co.jemos.podam.api.PodamFactoryImpl

@SpringBootTest
@ExtendWith(MockitoExtension::class)
class SignupServiceTests {
    private val factory = PodamFactoryImpl()

    private fun createFakeUserDto(): UserDto {
        return factory.manufacturePojo(UserDto::class.java)
    }

    @Test
    fun `Should sign an user`() {

    }
}