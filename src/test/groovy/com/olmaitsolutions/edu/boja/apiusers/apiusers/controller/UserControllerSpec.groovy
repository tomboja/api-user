package com.olmaitsolutions.edu.boja.apiusers.apiusers.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.CreateUserDTO
import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.UserDTO
import com.olmaitsolutions.edu.boja.apiusers.apiusers.exception.UserNotFoundException
import com.olmaitsolutions.edu.boja.apiusers.apiusers.service.UserService
import jakarta.servlet.ServletException
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserControllerSpec extends Specification {

    UserService userService = Mock()
    UserController controller = new UserController(userService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    ObjectMapper objectMapper = new ObjectMapper()

    def "createUser returns 201 and body on success"() {
        given:
        def dto = new CreateUserDTO(
                firstName: 'James',
                lastName: 'Hook',
                email: 'jhook@example.com',
                username: 'jhunter123',
                password: 'Password12345'
        )
        def response = new UserDTO(id: 1L, email: dto.email, username: dto.username)
        userService.createUser(_ as CreateUserDTO) >> response

        when:
        def resultActions = mockMvc.perform(post('/api/users')
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))

        then:
        resultActions.andExpect(status().isCreated())
                     .andExpect(jsonPath('$.id').value(1L))
                     .andExpect(jsonPath('$.email').value(dto.email))
    }

    def "getUser returns 200 when found"() {
        given:
        def dto = new UserDTO(id: 1L, email: 'test@example.com', username: 'testuser')
        userService.getUser(1L) >> dto

        when:
        def result = mockMvc.perform(get('/api/users/1'))

        then:
        result.andExpect(status().isOk())
              .andExpect(jsonPath('$.id').value(1L))
    }

   def "getUser propagates UserNotFoundException"() {
       given:
       userService.getUser(99L) >> { throw new UserNotFoundException('not found') }

       when:
       mockMvc.perform(get('/api/users/99'))

       then:
       def ex = thrown(ServletException)
       assert ex.cause instanceof UserNotFoundException
   }

    def "deleteUserById returns 204"() {
        when:
        def result = mockMvc.perform(delete('/api/users/1'))

        then:
        result.andExpect(status().isNoContent())
        1 * userService.deleteUserById(1L)
    }

    def "deleteUserByUsername returns 204"() {
        when:
        def result = mockMvc.perform(delete('/api/users').param('username', 'jhunter123'))

        then:
        result.andExpect(status().isNoContent())
        1 * userService.deleteUserByUsername('jhunter123')
    }
}
