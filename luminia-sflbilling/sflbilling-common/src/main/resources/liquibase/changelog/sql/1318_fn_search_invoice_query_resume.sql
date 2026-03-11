drop function if exists view_resume_transaction;
GO

CREATE OR REPLACE FUNCTION view_resume_transaction(IN in_date_begin timestamp without time zone, IN in_date_end timestamp without time zone, IN in_company integer)
RETURNS TABLE (
      quantity_emitted     int8,
      amount_emitted       float8,
      quantity_canceled    int8,
      amount_canceled      float8,
      quantity_reversed    int8,
      amount_reversed      float8,
      quantity_pending     int8,
      amount_pending       float8,
      quantity_observed    int8,
      amount_observed      float8
    ) AS $BODY$
DECLARE
    sql text := '
        select
              SUM(CASE WHEN i.status = ''EMITTED''  THEN 1 ELSE 0 END) as quantity_emitted,
              SUM(CASE WHEN i.status = ''EMITTED''  THEN ((i.invoice_json::json)->''cabecera''->>''montoTotal'')::float ELSE 0 END) as amount_emitted,
              SUM(CASE WHEN i.status = ''CANCELED''  THEN 1 ELSE 0 END) as quantity_canceled,
              SUM(CASE WHEN i.status = ''CANCELED''  THEN ((i.invoice_json::json)->''cabecera''->>''montoTotal'')::float ELSE 0 END) as amount_canceled,
              SUM(CASE WHEN i.status = ''REVERSED''  THEN 1 ELSE 0 END) as quantity_reversed,
              SUM(CASE WHEN i.status = ''REVERSED''  THEN ((i.invoice_json::json)->''cabecera''->>''montoTotal'')::float ELSE 0 END) as amount_reversed,
              SUM(CASE WHEN i.status = ''PENDING''  THEN 1 ELSE 0 END) as quantity_pending,
              SUM(CASE WHEN i.status = ''PENDING''  THEN ((i.invoice_json::json)->''cabecera''->>''montoTotal'')::float ELSE 0 END) as amount_pending,
              SUM(CASE WHEN i.status = ''OBSERVED''  THEN 1 ELSE 0 END) as quantity_observed,
              SUM(CASE WHEN i.status = ''OBSERVED''  THEN ((i.invoice_json::json)->''cabecera''->>''montoTotal'')::float ELSE 0 END) as amount_observed
        from invoice i
            inner join cufd c on c.id = i.cufd_id
            inner join cuis cs on cs.id = c.cuis_id
            inner join point_sale p on p.id = cs.point_sale_id
            inner join branch_office bo on bo.id = p.branch_office_id
            inner join company cp on cp.id = bo.company_id
        where i.broadcast_date >= '''|| in_date_begin  ||''' and i.broadcast_date <=  '''|| in_date_end  ||'''
              and bo.company_id  = ' || in_company || '
  ';
BEGIN
  RETURN QUERY EXECUTE sql;
END;
$BODY$ LANGUAGE plpgsql STRICT;
GO