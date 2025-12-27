package med.doctor_connect.service;

import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;

import java.util.UUID;

public interface StripeConnectService {

    Account createConnectAccount(UUID doctorProfileId, String email, String country);

    AccountLink createAccountOnboardingLink(UUID doctorProfileId, String refreshUrl, String returnUrl);

    LoginLink createDashboardLink(UUID doctorProfileId);

    Account getAccountStatus(UUID doctorProfileId);

    void updateAccountStatus(UUID doctorProfileId);

    boolean isAccountFullyOnboarded(UUID doctorProfileId);
}
