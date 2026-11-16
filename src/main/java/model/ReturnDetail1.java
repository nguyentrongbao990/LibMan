package model;

import java.math.BigDecimal;

public class ReturnDetail1 {
    private long id;
    private BigDecimal lateFine;
    private BigDecimal damageFine;
    private LoanDetail1 loanDetail;

    public ReturnDetail1() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public BigDecimal getLateFine() { return lateFine; }
    public void setLateFine(BigDecimal lateFine) { this.lateFine = lateFine; }

    public BigDecimal getDamageFine() { return damageFine; }
    public void setDamageFine(BigDecimal damageFine) { this.damageFine = damageFine; }

    public LoanDetail1 getLoanDetail() { return loanDetail; }
    public void setLoanDetail(LoanDetail1 loanDetail) { this.loanDetail = loanDetail; }
}
