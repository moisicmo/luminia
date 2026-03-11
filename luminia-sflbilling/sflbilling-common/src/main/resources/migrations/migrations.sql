-- v1 2022-08-07
create index "invoice_date_idx" on invoice ("broadcast_date");
create index "invoice_date_status_idx" on invoice ("broadcast_date", "status");
update databasechangelog set md5sum = null where id in ('1304-1');

-- v1 2022-10-18
alter table product_service alter column description type varchar(1000);
update databasechangelog set md5sum = null where id in ('1007-1');