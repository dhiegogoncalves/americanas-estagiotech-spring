package br.com.americanas.estagiotech.libraryapi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final String CRON_LATE_LOANS = "*/60 * * * * *";

    @Value("${application.mail.late-loan.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans() {
        log.info("Sending email to Late Loans");
        var lateLoans = loanService.getAllLateLoans();
        var mailList = lateLoans.stream().map(loan -> loan.getCustomerEmail()).toList();

        if (mailList.size() > 0) {
            emailService.sendMails(mailList, message);
        }
    }
}
