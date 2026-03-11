package bo.com.luminia.sflbilling.msbatch.service.dto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@NoArgsConstructor
public class TokenCertificateDto {

    @Setter
    @Getter
    private String company;

    @Setter
    @Getter
    private Long id;

    @Getter
    private Date endToken;

    @Getter
    private Date endCertificate;

    @Getter
    private int daysToExpireToken = 0;

    @Getter
    private int daysToExpireCertificate = 0;


    public boolean isExpired() {
        if (endCertificate == null || endToken == null)
            return true;
        Date d = new Date();
        return d.compareTo(endCertificate) > 0 ||
            d.compareTo(endToken) > 0;
    }

    public void setEndToken(Date d){
        this.endToken = d;
        this.daysToExpireToken = getDays(d);
    }

    public void setEndCertificate(Date d){
        this.endCertificate = d;
        this.daysToExpireCertificate = getDays(d);
    }

    private int getDays(Date d) {
        if (d == null)
            return -1;
        long diff = Math.abs(d.getTime() - (new Date()).getTime());
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }



}
