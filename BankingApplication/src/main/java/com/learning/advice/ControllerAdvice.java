package com.learning.advice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.exception.apierror.ApiError;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAdvice.class);
	
//	@ExceptionHandler(DataNotFoundException.class)	// is responsible for handling 
//	public ResponseEntity<?> dataNotFoundException(DataNotFoundException e) {
//		//Map<String,String> map = new HashMap<>();
//		//map.put("message", "no data found");
//		ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, e.getMessage(), e);
//		
//		return buildResponseEntity(apiError);
//	}
	
	// Takes care of validation errors implicitly
	// Will work with issues regarding @Valid (post method)
	@Override
	//@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		ApiError apiError = new ApiError(status);
		apiError.setMessage("validation error");
		apiError.addValidationErrors(ex.getFieldErrors());
		apiError.addValidationObjectErrors(ex.getBindingResult().getGlobalErrors());
		return buildResponseEntity(apiError);
	}
	
	private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
		return new ResponseEntity<Object>(apiError, apiError.getStatus());
	}
	
	// Will work with transformation of path variable to expected argument
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
		
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
		apiError.setMessage(e.getMessage());
		apiError.setDebugMessage(e.getRequiredType().getName());
		
		return buildResponseEntity(apiError);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
		
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST);
		apiError.setMessage(e.getMessage());
		
		return buildResponseEntity(apiError);
	}

	
	
	// Default handler
	// will run if there is no handler capable of handling the exception
//	@ExceptionHandler(Exception.class)
//	protected ResponseEntity<?> handleMethodException(Exception e) {
//		
//		ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
//		apiError.setMessage(e.getMessage());
//		
//		return buildResponseEntity(apiError);
//	}
}
