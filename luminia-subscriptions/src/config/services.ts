export class RMQServiceAuthentications {
  static getName() { return 'RMQ_SERVICE_AUTHENTICATIONS'; }
  static getQueueName() { return 'authentications_queue'; }
}

export class RMQServiceBusiness {
  static getName() { return 'RMQ_SERVICE_BUSINESS'; }
  static getQueueName() { return 'business_queue'; }
}

export class RMQServiceOrders {
  static getName() { return 'RMQ_SERVICE_ORDERS'; }
  static getQueueName() { return 'orders_queue'; }
}

export class RMQServicePayments {
  static getName() { return 'RMQ_SERVICE_PAYMENTS'; }
  static getQueueName() { return 'payments_queue'; }
}

export class RMQServiceStores {
  static getName() { return 'RMQ_SERVICE_STORES'; }
  static getQueueName() { return 'stores_queue'; }
}

export class RMQServiceSubscriptions {
  static getName() { return 'RMQ_SERVICE_SUBSCRIPTIONS'; }
  static getQueueName() { return 'subscriptions_queue'; }
}

export class RMQServiceUsers {
  static getName() { return 'RMQ_SERVICE_USERS'; }
  static getQueueName() { return 'users_queue'; }
}
