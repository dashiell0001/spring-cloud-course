package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return price quotes"
    name "shouldReturnValidPricingQuote"

    request {
        method 'GET'
        url('/api/pricing/quote') {
            queryParameters {
                parameter("baseFare", '100.00')
                parameter("currency", 'eur')
                parameter("bags", 2)
            }
        }
        headers {
            accept 'application/json'
            contentType 'application/json'
        }
    }

    response {
        status 200
        headers {
            contentType(applicationJson())
        }

        body ([
                baseFare: 100.00,
                tax: 21.00,
                bagFees: 60.00,
                totalFare: 181.00,
                currency: 'EUR',
                rulesVersion: 'v1'
        ])
    }
}