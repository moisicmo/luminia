package bo.com.luminia.sflbilling.msaccount.service.sfe;

public interface BaseCrudService<C, U, R> {

    public R create(C request);

    public R delete(Long companyId, Integer id);

    public R update(U request);

    public R get(Long companyId, Integer id);

}
