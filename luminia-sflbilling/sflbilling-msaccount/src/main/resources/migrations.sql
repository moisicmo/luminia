-- ---------------------------------------------------------
-- Migracion de sprint 5
update databasechangelog set author = 'jhipster' where author <> 'jhipster';

alter table product_service alter column description type varchar(500);
update databasechangelog set md5sum = null where id in ('1007-1');

alter table invoice_wrapper drop constraint fk_invoice_wrapper_invoice_id;
alter table invoice_wrapper rename column invoice_id to invoice_batch_id;
update databasechangelog set md5sum = null where id in ('1309-1', '1309-2');

-- Despues de levantar una vez el backend ya que la tabla aun no existe
alter table invoice_wrapper add constraint fk_invoice_wrapper_invoice_batch_id foreign key ("invoice_batch_id") references invoice_batch("id") on delete no action on update no action;
-- ---------------------------------------------------------
