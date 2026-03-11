drop function if exists view_amount_from_invoice;
GO

CREATE OR REPLACE FUNCTION view_amount_from_invoice(IN in_invoice_id int8)
RETURNS TABLE (
    amount float8
) AS $BODY$
DECLARE
    sql text := '
        select ((invoice_json::json)->''cabecera''->>''montoTotal'')::float8
        from invoice where id = ' || in_invoice_id || ''
    ;
BEGIN
RETURN QUERY EXECUTE sql;
END;
$BODY$ LANGUAGE plpgsql STRICT;
GO
