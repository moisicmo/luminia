import 'dotenv/config';

// luminia-orders no requiere datos semilla estáticos.
//
// Order, OrderItem y OrderPayment son registros transaccionales
// generados en tiempo de ejecución por los negocios y sus clientes.
// No existe un catálogo fijo de órdenes a precargar.

async function main() {
  console.log('Seeding luminia-orders...');
  console.log('  (sin datos semilla estáticos — las órdenes se crean en tiempo de ejecución)');
  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
