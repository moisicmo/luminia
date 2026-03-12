import 'dotenv/config';

// luminia-files no requiere datos semilla estáticos.
//
// File y Document son registros generados dinámicamente por los
// microservicios (órdenes, facturas, reportes, etc.) y los usuarios.
// No existe un catálogo fijo de archivos o documentos a precargar.

function main() {
  console.log('Seeding luminia-files...');
  console.log('  (sin datos semilla estáticos — los archivos se crean en tiempo de ejecución)');
  console.log('Done!');
}

main();
