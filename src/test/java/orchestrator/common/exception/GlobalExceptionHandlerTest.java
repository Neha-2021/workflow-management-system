package orchestrator.common.exception;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  @Test
  void shouldHandleGenericException() {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    RuntimeException exception = new RuntimeException("Database down");

    ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(500);
    assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
    assertThat(response.getBody().getErrorMessage()).isEqualTo("Unexpected error occurred");
  }

  @Test
  void shouldHandleInvalidWorkflowDefinitionException() {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    InvalidWorkflowDefinitionException exception =
        new InvalidWorkflowDefinitionException("Duplicate sequence numbers");

    ResponseEntity<ErrorResponse> response =
        handler.handleInvalidWorkflowDefinitionException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getError()).isEqualTo("Invalid workflow definition request");
    assertThat(response.getBody().getErrorMessage()).isEqualTo("Duplicate sequence numbers");
  }

  @Test
  void shouldHandleMethodArgumentNotValidException() {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

    BindingResult bindingResult = mock(BindingResult.class);

    FieldError fieldError = new FieldError("workflow", "name", "must not be blank");

    given(exception.getBindingResult()).willReturn(bindingResult);
    given(bindingResult.getFieldErrors()).willReturn(List.of(fieldError));

    ResponseEntity<ErrorResponse> response =
        handler.handleInvalidMethodArgumentException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getError()).isEqualTo("Invalid request");
    assertThat(response.getBody().getErrorMessage()).isEqualTo("name: must not be blank");
  }
}
