package com.tech.exception;

import javax.security.sasl.AuthenticationException;
import javax.ws.rs.core.Response;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class ExceptionTranslater implements ProblemHandling {
	
	@Override
	public ResponseEntity<Problem> process(ResponseEntity<Problem> entity) {
		Problem body = entity.getBody();
		ProblemBuilder builder = Problem.builder().withType(body.getType())
				.with("message", body.getTitle());
		return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
	}
	
	@ExceptionHandler
	public ResponseEntity<Problem> throwAuthenticationException(AuthenticationException ex,
			NativeWebRequest request) {
		ThrowableProblem build = Problem.builder().withStatus(Response.Status.BAD_REQUEST).with("Nodata found", ex.getMessage())
				.build();
		return create(build, request);
	}
}
