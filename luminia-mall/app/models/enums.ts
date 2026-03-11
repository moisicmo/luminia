export enum TypeAction {
  manage = 'manage',
  create = 'create',
  read   = 'read',
  update = 'update',
  delete = 'delete',
}

export enum TypeSubject {
  branch       = 'branch',
  staff        = 'staff',
  role         = 'role',
  permission   = 'permission',
  user         = 'user',
  attendance   = 'attendance',
  inscription  = 'inscription',
  booking      = 'booking',
  room         = 'room',
  specialty    = 'specialty',
  teacher      = 'teacher',
  student      = 'student',
  tutor        = 'tutor',
  payment      = 'payment',
  report       = 'report',
  correspondence = 'correspondence',
}

export enum DayOfWeek {
  MONDAY    = 'Lunes',
  TUESDAY   = 'Martes',
  WEDNESDAY = 'Miércoles',
  THURSDAY  = 'Jueves',
  FRIDAY    = 'Viernes',
  SATURDAY  = 'Sábado',
  SUNDAY    = 'Domingo',
}

export enum TypeDebt {
  INSCRIPTION = 'Inscripción',
  MONTH       = 'Mensualidad',
  BOOKING     = 'Reserva',
  PER_SESSION = 'Por sesión',
}

export enum PayMethod {
  CASH          = 'Efectivo',
  QR            = 'QR',
  BANK_TRANSFER = 'Transferencia',
  CARD          = 'Tarjeta',
}

export enum BusinessType {
  GYM          = 'GYM',
  STORE        = 'STORE',
  RESTAURANT   = 'RESTAURANT',
  IT_SERVICES  = 'IT_SERVICES',
  PHARMACY     = 'PHARMACY',
  BOOKSTORE    = 'BOOKSTORE',
  HARDWARE     = 'HARDWARE',
  NATURIST     = 'NATURIST',
  BEAUTY       = 'BEAUTY',
  EDUCATION    = 'EDUCATION',
  OTHER        = 'OTHER',
}
