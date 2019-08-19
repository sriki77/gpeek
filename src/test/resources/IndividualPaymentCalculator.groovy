import org.sriki.gpeek.intercept.Intercept
import org.sriki.gpeek.testData.PaymentCalculator

import java.sql.Date
import java.time.LocalDate

class IndividualPaymentCalculator implements PaymentCalculator {

    BigDecimal servantPay
    BigDecimal schoolFees
    BigDecimal propertyTax
    BigDecimal totalPayment

    IndividualPaymentCalculator() {
        servantPay = 3000.0
        schoolFees = 150000.0
        propertyTax = 30000.0
    }

    IndividualPaymentCalculator(BigDecimal servant, BigDecimal schoolFees, BigDecimal propertyTax) {
        this.servantPay = servant
        this.schoolFees = schoolFees
        this.propertyTax = propertyTax
    }

    static def isMonth(sqlDate, int ... value) {
        LocalDate date = sqlDate.toLocalDate()
        return value.contains(date.monthValue)
    }


    @Intercept
    BigDecimal payment() {
        def now = LocalDate.now()
        def march = isMarch(this, Date.valueOf(now))
        if (march) {
            return schoolFees + propertyTax
        }
        return servantPay
    }

    @Intercept
    static boolean isMarch(obj, Date date) {
        final int MARCH = 3
        obj.isMonth(date, MARCH)
    }

    @Intercept
    void paymentSet() {
        def now = LocalDate.now()
        def march = isMarch(this, Date.valueOf(now))
        if (march) {
            this.totalPayment = this.schoolFees + this.propertyTax
            return
        }
        this.totalPayment = servantPay
    }

    @Intercept
    void paymentMarch() {
        def now = LocalDate.now()
        def march = isMarch(this, Date.valueOf(now))
        if (!march) {
            this.totalPayment = this?.schoolFees + this.propertyTax
            return
        }
        this.totalPayment = servantPay
    }

}