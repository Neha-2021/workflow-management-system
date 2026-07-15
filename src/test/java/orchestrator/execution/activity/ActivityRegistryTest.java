package orchestrator.execution.activity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.List;
import orchestrator.common.exception.ActivityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityRegistryTest {

  @Mock private Activity activity;

  @Test
  void shouldReturnRegisteredActivity() {

    given(activity.getName()).willReturn(ActivityNames.VALIDATE_ORDER);

    ActivityRegistry registry = new ActivityRegistry(List.of(activity));

    Activity resolved = registry.getActivity(ActivityNames.VALIDATE_ORDER);

    assertThat(resolved).isEqualTo(activity);
  }

  @Test
  void shouldResolveOrderWorkflowActivities() {
    ProcessPaymentActivityImpl processPaymentActivity = new ProcessPaymentActivityImpl();
    NotifyCustomerActivityImpl notifyCustomerActivity = new NotifyCustomerActivityImpl();
    SendEmailActivityImpl sendEmailActivity = new SendEmailActivityImpl();

    ActivityRegistry registry =
        new ActivityRegistry(
            List.of(processPaymentActivity, notifyCustomerActivity, sendEmailActivity));

    assertThat(registry.getActivity(ActivityNames.PROCESS_PAYMENT))
        .isEqualTo(processPaymentActivity);
    assertThat(registry.getActivity(ActivityNames.NOTIFY_CUSTOMER))
        .isEqualTo(notifyCustomerActivity);
    assertThat(registry.getActivity(ActivityNames.SEND_EMAIL)).isEqualTo(sendEmailActivity);
  }

  @Test
  void shouldThrowWhenActivityNotRegistered() {

    ActivityRegistry registry = new ActivityRegistry(List.of());

    Throwable exception =
        assertThrows(ActivityNotFoundException.class, () -> registry.getActivity("UNKNOWN"));

    assertThat(exception.getMessage()).isEqualTo("Activity 'UNKNOWN' not registered");
  }
}
