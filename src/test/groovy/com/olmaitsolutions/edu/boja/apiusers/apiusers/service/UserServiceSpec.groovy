package com.olmaitsolutions.edu.boja.apiusers.apiusers.service

import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.CreateUserDTO;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.UserDTO;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.exception.UserNotFoundException;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.mapper.UserMapper;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.model.User;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import spock.lang.Specification;

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    UserMapper userMapper = Mock()
    PasswordEncoder passwordEncoder = Mock()

    UserService userService = new UserService(userRepository, userMapper, passwordEncoder)

def "createUser encodes password, sets defaults and saves user"() {
    given:
    def dto = new CreateUserDTO(
            firstName: 'James',
            lastName: 'Hook',
            email: 'jhook@example.com',
            username: 'jhunter123',
            password: 'Password1',
            phoneNumber: '0413100978',
            userRole: 'ADMIN'
    )
    def user = User.builder().build()
    def saved = User.builder()
            .id(1L)
            .email(dto.email)
            .username(dto.username)
            .userRole('ADMIN')
            .build()
    def expectedDto = new UserDTO(id: 1L, email: dto.email, username: dto.username, userRole: 'ADMIN')

    and:
    userMapper.toEntity(dto) >> user
    passwordEncoder.encode(dto.password) >> 'ENCODED'
    userRepository.save(user) >> saved
    userMapper.toDto(saved) >> expectedDto

    when:
    def result = userService.createUser(dto)

    then:
    1 * passwordEncoder.encode(dto.password)
    user.createdDate != null
    user.emailVerified == false
    user.activeStatus == false
    user.userRole == 'USER'

    and:
    result.id == 1L
    result.email == dto.email
    result.username == dto.username
    result.userRole == 'ADMIN'
}

    def "getUser returns DTO when found"() {
        given:
        def user = User.builder()
                .id(1L)
                .email('test@example.com')
                .username('testuser')
                .build()
        def dto = new UserDTO(id: 1L, email: user.email, username: user.username)

        and:
        userRepository.findById(1L) >> Optional.of(user)
        userMapper.toDto(user) >> dto

        expect:
        userService.getUser(1L) == dto
    }

    def "getUser throws UserNotFoundException when not found"() {
        given:
        userRepository.findById(99L) >> Optional.empty()

        when:
        userService.getUser(99L)

        then:
        def ex = thrown(UserNotFoundException)
        ex.message.contains('99')
    }

    def "deleteUserById deletes existing user"() {
        given:
        def user = User.builder().id(1L).build()
        userRepository.findById(1L) >> Optional.of(user)

        when:
        userService.deleteUserById(1L)

        then:
        1 * userRepository.delete(user)
    }

    def "deleteUserById throws when id null"() {
        when:
        userService.deleteUserById(null)

        then:
        thrown(UserNotFoundException)
    }

    def "deleteUserByUsername deletes existing user"() {
        given:
        def user = User.builder().id(1L).username('jhunter123').build()
        userRepository.findByUsername('jhunter123') >> Optional.of(user)

        when:
        userService.deleteUserByUsername('jhunter123')

        then:
        1 * userRepository.delete(user)
    }

    def "deleteUserByUsername throws when blank"() {
        when:
        userService.deleteUserByUsername(' ')

        then:
        thrown(UserNotFoundException)
    }

    def "deleteUserByUsername throws when not found"() {
        given:
        userRepository.findByUsername('missing') >> Optional.empty()

        when:
        userService.deleteUserByUsername('missing')

        then:
        thrown(UserNotFoundException)
    }
}
