package med.doctor_connect.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import med.doctor_connect.model.DoctorProfile;
import med.doctor_connect.repository.DoctorProfileRepository;
import med.doctor_connect.service.StripeConnectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeConnectServiceImpl implements StripeConnectService {

    private final DoctorProfileRepository doctorProfileRepository;

    @Override
    @Transactional
    public Account createConnectAccount(UUID doctorProfileId, String email, String country) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorProfileId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        if (doctorProfile.getStripeConnectAccountId() != null) {
            throw new RuntimeException("Doctor already has a Stripe Connect account");
        }

        try {
            AccountCreateParams params = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setCountry(country != null ? country : "US")
                    .setEmail(email)
                    .setCapabilities(
                            AccountCreateParams.Capabilities.builder()
                                    .setCardPayments(
                                            AccountCreateParams.Capabilities.CardPayments.builder()
                                                    .setRequested(true)
                                                    .build()
                                    )
                                    .setTransfers(
                                            AccountCreateParams.Capabilities.Transfers.builder()
                                                    .setRequested(true)
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Account account = Account.create(params);

            doctorProfile.setStripeConnectAccountId(account.getId());
            doctorProfile.setStripeAccountStatus("created");
            doctorProfile.setStripeOnboardingCompleted(false);
            doctorProfileRepository.save(doctorProfile);

            log.info("Created Stripe Connect account {} for doctor {}", account.getId(), doctorProfileId);
            return account;

        } catch (StripeException e) {
            log.error("Error creating Stripe Connect account for doctor {}: {}", doctorProfileId, e.getMessage(), e);
            throw new RuntimeException("Failed to create Stripe Connect account: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AccountLink createAccountOnboardingLink(UUID doctorProfileId, String refreshUrl, String returnUrl) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorProfileId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        if (doctorProfile.getStripeConnectAccountId() == null) {
            throw new RuntimeException("Doctor does not have a Stripe Connect account. Create one first.");
        }

        try {
            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(doctorProfile.getStripeConnectAccountId())
                    .setRefreshUrl(refreshUrl)
                    .setReturnUrl(returnUrl)
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink accountLink = AccountLink.create(params);
            log.info("Created onboarding link for doctor {}", doctorProfileId);

            return accountLink;

        } catch (StripeException e) {
            log.error("Error creating onboarding link for doctor {}: {}", doctorProfileId, e.getMessage(), e);
            throw new RuntimeException("Failed to create onboarding link: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginLink createDashboardLink(UUID doctorProfileId) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorProfileId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        if (doctorProfile.getStripeConnectAccountId() == null) {
            throw new RuntimeException("Doctor does not have a Stripe Connect account");
        }

        if (!doctorProfile.isStripeOnboardingCompleted()) {
            throw new RuntimeException("Doctor has not completed Stripe onboarding");
        }

        try {
            Account account = Account.retrieve(doctorProfile.getStripeConnectAccountId());
            LoginLink loginLink = LoginLink.createOnAccount(account.getId());

            log.info("Created dashboard link for doctor {}", doctorProfileId);
            return loginLink;

        } catch (StripeException e) {
            log.error("Error creating dashboard link for doctor {}: {}", doctorProfileId, e.getMessage(), e);
            throw new RuntimeException("Failed to create dashboard link: " + e.getMessage(), e);
        }
    }

    @Override
    public Account getAccountStatus(UUID doctorProfileId) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorProfileId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        if (doctorProfile.getStripeConnectAccountId() == null) {
            throw new RuntimeException("Doctor does not have a Stripe Connect account");
        }

        try {
            return Account.retrieve(doctorProfile.getStripeConnectAccountId());
        } catch (StripeException e) {
            log.error("Error retrieving account status for doctor {}: {}", doctorProfileId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve account status: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateAccountStatus(UUID doctorProfileId) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorProfileId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        if (doctorProfile.getStripeConnectAccountId() == null) {
            throw new RuntimeException("Doctor does not have a Stripe Connect account");
        }

        try {
            Account account = Account.retrieve(doctorProfile.getStripeConnectAccountId());

            boolean chargesEnabled = account.getChargesEnabled();
            boolean payoutsEnabled = account.getPayoutsEnabled();
            boolean detailsSubmitted = account.getDetailsSubmitted();

            doctorProfile.setStripeAccountStatus(
                    chargesEnabled && payoutsEnabled ? "active" : "pending"
            );
            doctorProfile.setStripeOnboardingCompleted(detailsSubmitted && chargesEnabled);

            doctorProfileRepository.save(doctorProfile);

            log.info("Updated account status for doctor {}: {}", doctorProfileId, doctorProfile.getStripeAccountStatus());

        } catch (StripeException e) {
            log.error("Error updating account status for doctor {}: {}", doctorProfileId, e.getMessage(), e);
            throw new RuntimeException("Failed to update account status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAccountFullyOnboarded(UUID doctorProfileId) {
        DoctorProfile doctorProfile = doctorProfileRepository.findById(doctorProfileId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        return doctorProfile.isStripeOnboardingCompleted();
    }
}
