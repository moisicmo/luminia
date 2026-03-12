import 'dotenv/config';
import { prisma } from '@/lib/prisma';

// UUID fijo del administrador raíz — debe coincidir con luminia-users seed
const LUMINIA_PERSON_ID = '00000000-0000-0000-0000-000000000001';

// ─── Templates globales (businessId = null → disponibles para todos los negocios) ─
// Canal EMAIL — las más críticas para el sistema

const TEMPLATES = [
  {
    id: '00000000-0000-0000-0008-000000000001',
    event: 'REGISTER_WELCOME',
    name: 'Bienvenida al registro',
    subject: '¡Bienvenido a {{businessName}}!',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>¡Hola, {{name}}! 👋</h2>
  <p>Nos alegra tenerte en <strong>{{businessName}}</strong>.</p>
  <p>Tu cuenta ha sido creada exitosamente. Ya puedes iniciar sesión y empezar a disfrutar de todos nuestros servicios.</p>
  <p style="margin-top: 30px;">Si no creaste esta cuenta, puedes ignorar este correo.</p>
  <p>Saludos,<br><strong>El equipo de {{businessName}}</strong></p>
</body>
</html>`,
    variables: ['name', 'businessName'],
  },
  {
    id: '00000000-0000-0000-0008-000000000002',
    event: 'VERIFY_EMAIL',
    name: 'Verificación de correo',
    subject: 'Verifica tu correo electrónico — código: {{code}}',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Verifica tu correo</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Usa el siguiente código para verificar tu dirección de correo:</p>
  <div style="background: #f4f4f4; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0;">
    <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #333;">{{code}}</span>
  </div>
  <p>Este código expira en <strong>{{expiresIn}}</strong>.</p>
  <p>Si no solicitaste esto, ignora este correo.</p>
</body>
</html>`,
    variables: ['name', 'code', 'expiresIn'],
  },
  {
    id: '00000000-0000-0000-0008-000000000003',
    event: 'FORGOT_PASSWORD',
    name: 'Restablecer contraseña',
    subject: 'Restablece tu contraseña',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Restablecer contraseña</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta.</p>
  <p>Usa el siguiente código:</p>
  <div style="background: #f4f4f4; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0;">
    <span style="font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #333;">{{code}}</span>
  </div>
  <p>Este código expira en <strong>{{expiresIn}}</strong>.</p>
  <p>Si no solicitaste el cambio de contraseña, tu cuenta sigue segura y puedes ignorar este correo.</p>
</body>
</html>`,
    variables: ['name', 'code', 'expiresIn'],
  },
  {
    id: '00000000-0000-0000-0008-000000000004',
    event: 'LOGIN_ALERT',
    name: 'Alerta de inicio de sesión',
    subject: 'Nuevo inicio de sesión en tu cuenta',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Nuevo inicio de sesión detectado</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Se detectó un nuevo inicio de sesión en tu cuenta:</p>
  <ul>
    <li><strong>Fecha:</strong> {{date}}</li>
    <li><strong>Dispositivo:</strong> {{device}}</li>
    <li><strong>Ubicación:</strong> {{location}}</li>
  </ul>
  <p>Si fuiste tú, puedes ignorar este mensaje. Si no reconoces esta actividad, cambia tu contraseña de inmediato.</p>
</body>
</html>`,
    variables: ['name', 'date', 'device', 'location'],
  },
  {
    id: '00000000-0000-0000-0008-000000000005',
    event: 'ORDER_CONFIRMED',
    name: 'Pedido confirmado',
    subject: 'Tu pedido #{{orderNumber}} fue confirmado ✅',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>¡Pedido confirmado!</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Tu pedido <strong>#{{orderNumber}}</strong> ha sido confirmado.</p>
  <p><strong>Total:</strong> Bs {{total}}</p>
  <p><strong>Fecha:</strong> {{date}}</p>
  <p>Gracias por tu compra en <strong>{{businessName}}</strong>.</p>
</body>
</html>`,
    variables: ['name', 'orderNumber', 'total', 'date', 'businessName'],
  },
  {
    id: '00000000-0000-0000-0008-000000000006',
    event: 'RECEIPT',
    name: 'Nota de venta',
    subject: 'Comprobante de venta #{{orderNumber}}',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Comprobante de venta</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Adjuntamos el comprobante de tu compra <strong>#{{orderNumber}}</strong>.</p>
  <p><strong>Total pagado:</strong> Bs {{total}}</p>
  <p><strong>Método de pago:</strong> {{paymentMethod}}</p>
  <p><strong>Fecha:</strong> {{date}}</p>
  <p>Gracias por elegirnos.</p>
</body>
</html>`,
    variables: ['name', 'orderNumber', 'total', 'paymentMethod', 'date'],
  },
  {
    id: '00000000-0000-0000-0008-000000000007',
    event: 'PAYMENT_RECEIVED',
    name: 'Pago recibido',
    subject: 'Pago recibido — Bs {{amount}}',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Pago recibido ✅</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Confirmamos la recepción de tu pago:</p>
  <ul>
    <li><strong>Monto:</strong> Bs {{amount}}</li>
    <li><strong>Referencia:</strong> {{reference}}</li>
    <li><strong>Fecha:</strong> {{date}}</li>
  </ul>
  <p>Gracias por tu pago.</p>
</body>
</html>`,
    variables: ['name', 'amount', 'reference', 'date'],
  },
  {
    id: '00000000-0000-0000-0008-000000000008',
    event: 'PAYMENT_FAILED',
    name: 'Pago fallido',
    subject: 'No pudimos procesar tu pago',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Pago no procesado ❌</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>No pudimos procesar tu pago de <strong>Bs {{amount}}</strong>.</p>
  <p><strong>Motivo:</strong> {{reason}}</p>
  <p>Por favor intenta nuevamente o contacta a soporte.</p>
</body>
</html>`,
    variables: ['name', 'amount', 'reason'],
  },
  {
    id: '00000000-0000-0000-0008-000000000009',
    event: 'DEBT_REMINDER',
    name: 'Recordatorio de deuda pendiente',
    subject: 'Tienes un saldo pendiente de Bs {{amount}}',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Recordatorio de pago</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Te recordamos que tienes un saldo pendiente:</p>
  <ul>
    <li><strong>Monto:</strong> Bs {{amount}}</li>
    <li><strong>Vencimiento:</strong> {{dueDate}}</li>
  </ul>
  <p>Por favor realiza tu pago antes del vencimiento.</p>
</body>
</html>`,
    variables: ['name', 'amount', 'dueDate'],
  },
  {
    id: '00000000-0000-0000-0008-000000000010',
    event: 'SUBSCRIPTION_CREATED',
    name: 'Suscripción activada',
    subject: 'Tu suscripción {{planName}} está activa 🎉',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>¡Suscripción activada!</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Tu suscripción al plan <strong>{{planName}}</strong> ha sido activada exitosamente.</p>
  <ul>
    <li><strong>Plan:</strong> {{planName}}</li>
    <li><strong>Precio:</strong> Bs {{price}} / {{billingCycle}}</li>
    <li><strong>Próxima facturación:</strong> {{nextBillingDate}}</li>
  </ul>
  <p>¡Bienvenido a bordo!</p>
</body>
</html>`,
    variables: ['name', 'planName', 'price', 'billingCycle', 'nextBillingDate'],
  },
  {
    id: '00000000-0000-0000-0008-000000000011',
    event: 'SUBSCRIPTION_EXPIRING',
    name: 'Suscripción por vencer',
    subject: 'Tu suscripción vence en {{daysLeft}} días',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Tu suscripción está por vencer</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Tu suscripción al plan <strong>{{planName}}</strong> vence en <strong>{{daysLeft}} días</strong> ({{expiryDate}}).</p>
  <p>Renueva ahora para no perder el acceso a tus servicios.</p>
</body>
</html>`,
    variables: ['name', 'planName', 'daysLeft', 'expiryDate'],
  },
  {
    id: '00000000-0000-0000-0008-000000000012',
    event: 'SUBSCRIPTION_EXPIRED',
    name: 'Suscripción expirada',
    subject: 'Tu suscripción {{planName}} ha expirado',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Suscripción expirada</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Tu suscripción al plan <strong>{{planName}}</strong> ha expirado.</p>
  <p>Renueva tu plan para seguir disfrutando de todos los beneficios.</p>
</body>
</html>`,
    variables: ['name', 'planName'],
  },
  {
    id: '00000000-0000-0000-0008-000000000013',
    event: 'CLASS_BOOKING_CONFIRMED',
    name: 'Reserva de clase confirmada',
    subject: 'Reserva confirmada: {{className}} 🏋️',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>¡Reserva confirmada!</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Tu reserva ha sido confirmada:</p>
  <ul>
    <li><strong>Clase:</strong> {{className}}</li>
    <li><strong>Instructor:</strong> {{instructor}}</li>
    <li><strong>Fecha y hora:</strong> {{startAt}}</li>
    <li><strong>Duración:</strong> {{durationMin}} minutos</li>
    <li><strong>Sala:</strong> {{location}}</li>
  </ul>
  <p>¡Te esperamos!</p>
</body>
</html>`,
    variables: ['name', 'className', 'instructor', 'startAt', 'durationMin', 'location'],
  },
  {
    id: '00000000-0000-0000-0008-000000000014',
    event: 'CLASS_REMINDER',
    name: 'Recordatorio de clase',
    subject: 'Recordatorio: {{className}} en {{timeUntil}}',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Tu clase es pronto ⏰</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Recuerda que tienes <strong>{{className}}</strong> en <strong>{{timeUntil}}</strong>.</p>
  <ul>
    <li><strong>Hora:</strong> {{startAt}}</li>
    <li><strong>Sala:</strong> {{location}}</li>
  </ul>
  <p>¡Nos vemos en el gym!</p>
</body>
</html>`,
    variables: ['name', 'className', 'timeUntil', 'startAt', 'location'],
  },
  {
    id: '00000000-0000-0000-0008-000000000015',
    event: 'CLASS_CANCELLED',
    name: 'Clase cancelada',
    subject: 'La clase {{className}} ha sido cancelada',
    body: `<!DOCTYPE html>
<html>
<body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
  <h2>Clase cancelada</h2>
  <p>Hola <strong>{{name}}</strong>,</p>
  <p>Lamentamos informarte que la clase <strong>{{className}}</strong> programada para el <strong>{{startAt}}</strong> ha sido cancelada.</p>
  <p><strong>Motivo:</strong> {{reason}}</p>
  <p>Disculpa los inconvenientes.</p>
</body>
</html>`,
    variables: ['name', 'className', 'startAt', 'reason'],
  },
] as const;

async function main() {
  console.log('Seeding luminia-notifiers...');

  let count = 0;
  for (const t of TEMPLATES) {
    await prisma.notificationTemplate.upsert({
      where: { id: t.id },
      update: {},
      create: {
        id: t.id,
        businessId: null,
        channel: 'EMAIL',
        event: t.event,
        name: t.name,
        subject: t.subject,
        body: t.body,
        variables: [...t.variables],
        active: true,
        createdBy: LUMINIA_PERSON_ID,
      },
    });
    count++;
  }
  console.log(`  NotificationTemplates: ${count} plantillas globales (EMAIL)`);

  console.log('Done!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => prisma.$disconnect());
