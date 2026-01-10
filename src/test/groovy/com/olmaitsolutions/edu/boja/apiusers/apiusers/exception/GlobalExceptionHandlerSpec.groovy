package com.olmaitsolutions.edu.boja.apiusers.apiusers.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import jakarta.validation.Path
import org.springframework.http.HttpStatus
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest
import spock.lang.Specification

class GlobalExceptionHandlerSpec extends Specification {

    GlobalExceptionHandler handler = new GlobalExceptionHandler()

    def "handleMethodArgumentNotValid builds ApiError with field errors"() {
        given:
        def target = new Object()
        def bindingResult = new BeanPropertyBindingResult(target, 'createUserDTO')
        bindingResult.addError(new FieldError('createUserDTO', 'username', 'qw', false,
                null, null, 'Username must be between 8 and 50 characters long'))

        def ex = new MethodArgumentNotValidException(null, bindingResult)
        WebRequest request = Stub(WebRequest) {
            getDescription(false) >> 'uri=/api/users'
        }

        when:
        def response = handler.handleMethodArgumentNotValid(ex, null, HttpStatus.BAD_REQUEST, request)
        def body = response.body as ApiError

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        body.message == 'Validation failed'
        body.path == 'uri=/api/users'
        body.fieldErrors.size() == 1
        body.fieldErrors[0].field == 'username'
        body.fieldErrors[0].message == 'Username must be between 8 and 50 characters long'
        body.fieldErrors[0].rejectedValue == 'qw'
    }

    def "handleConstraintViolation builds ApiError with violations"() {
        given:
        def path = Stub(Path) {
            toString() >> 'username'
        }
        ConstraintViolation<?> violation = Stub(ConstraintViolation) {
            getPropertyPath() >> path
            getMessage() >> 'Username is required'
            getInvalidValue() >> ''
        }
        def violations = new HashSet<ConstraintViolation<?>>()
        violations.add(violation)
        def ex = new ConstraintViolationException(violations)
        HttpServletRequest request = Stub(HttpServletRequest) {
            getRequestURI() >> '/api/users'
        }

        when:
        def response = handler.handleConstraintViolation(ex, request)
        def body = response.body as ApiError

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        body.message == 'Validation failed'
        body.path == '/api/users'
        body.fieldErrors.size() == 1
        body.fieldErrors[0].field == 'username'
        body.fieldErrors[0].message == 'Username is required'
        body.fieldErrors[0].rejectedValue == ''
    }

    def "handleUserNotFound returns 404 with message"() {
        given:
        def ex = new UserNotFoundException('User with id 99 not found')
        HttpServletRequest request = Stub(HttpServletRequest) {
            getRequestURI() >> '/api/users/99'
        }

        when:
        def response = handler.handleUserNotFound(ex, request)
        def body = response.body as ApiError

        then:
        response.statusCode == HttpStatus.NOT_FOUND
        body.message == 'User with id 99 not found'
        body.path == '/api/users/99'
    }

    def "handleGenericException unwraps root cause message"() {
        given:
        def root = new RuntimeException('ERROR: duplicate key value violates unique constraint "users_email_key"')
        def ex = new RuntimeException('Wrapper', root)
        HttpServletRequest request = Stub(HttpServletRequest) {
            getRequestURI() >> '/api/users'
        }

        when:
        def response = handler.handleGenericException(ex, request)
        def body = response.body as ApiError

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        body.message.contains('duplicate key value violates unique constraint')
        body.path == '/api/users'
    }
}
