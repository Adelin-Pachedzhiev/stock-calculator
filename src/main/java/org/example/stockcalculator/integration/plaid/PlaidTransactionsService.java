package org.example.stockcalculator.integration.plaid;

import static com.plaid.client.model.CountryCode.ES;
import static com.plaid.client.model.CountryCode.GB;
import static com.plaid.client.model.CountryCode.NL;
import static com.plaid.client.model.CountryCode.US;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.plaid.client.model.InstitutionsGetByIdRequest;
import com.plaid.client.model.InstitutionsGetByIdRequestOptions;
import com.plaid.client.model.InstitutionsGetByIdResponse;
import com.plaid.client.model.InvestmentsTransactionsGetRequest;
import com.plaid.client.request.PlaidApi;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Response;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlaidTransactionsService {

    private final PlaidApi plaidApi;
    private final PlaidProperties properties;

//    @PostConstruct
    public void init() throws IOException {
        InvestmentsTransactionsGetRequest investmentsTransactionsGetRequest = new InvestmentsTransactionsGetRequest()
                .startDate(LocalDate.now().minusYears(2))
                .endDate(LocalDate.now())
                .accessToken("access-production-770789a1-0762-4852-8703-18180e64d6b7");

        var investmentsResponse = plaidApi.investmentsTransactionsGet(investmentsTransactionsGetRequest)
                .execute();

        log.info("Investments transactions response: {}", investmentsResponse.body());
    }


//    @PostConstruct
    public void institutionsGet() throws IOException {
        InstitutionsGetByIdRequestOptions options = new InstitutionsGetByIdRequestOptions()
                .includeOptionalMetadata(true);
        InstitutionsGetByIdRequest institutionsGetByIdRequest = new InstitutionsGetByIdRequest()
                .institutionId("ins_132958")
                .options(options)
                .countryCodes(List.of(US, GB, ES, NL))
                .secret(properties.secret())
                .clientId(properties.clientId());

        Response<InstitutionsGetByIdResponse> execute = plaidApi.institutionsGetById(institutionsGetByIdRequest).execute();
        log.info("Institutions by ID response code: {}", execute.code());
        log.info("Institutions by ID response error: {}", execute.message());
        log.info("Institutions by ID response: {}", execute.body());

//        InstitutionsGetRequestOptions options = new InstitutionsGetRequestOptions()
//                .includeOptionalMetadata(true);
//        InstitutionsGetRequest institutionsGetRequest = new InstitutionsGetRequest()
//                .options(options)
//                .count(500)
//                .countryCodes(List.of(US, GB, ES, NL))
//                .offset(0)
//                .secret(properties.secret())
//                .clientId(properties.clientId());
//
//        var investmentsResponse = plaidApi.institutionsGet(institutionsGetRequest)
//                .execute();
//        log.info("Institutions response code: {}", investmentsResponse.code());
//        log.info("Institutions response error: {}", investmentsResponse.message());
//        log.info("Investments transactions response: {}", investmentsResponse.body());
    }
}
