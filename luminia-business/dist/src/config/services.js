"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.RMQServiceUsers = exports.RMQServiceSubscriptions = exports.RMQServiceStores = exports.RMQServicePayments = exports.RMQServiceOrders = exports.RMQServiceNotifiers = exports.RMQServiceGyms = exports.RMQServiceFiles = exports.RMQServiceBusiness = exports.RMQServiceBillers = exports.RMQServiceAuthentications = void 0;
class RMQServiceAuthentications {
    static getName() {
        return 'RMQ_SERVICE_AUTHENTICATIONS';
    }
    static getQueueName() {
        return 'authentications_queue';
    }
}
exports.RMQServiceAuthentications = RMQServiceAuthentications;
class RMQServiceBillers {
    static getName() {
        return 'RMQ_SERVICE_BILLERS';
    }
    static getQueueName() {
        return 'billers_queue';
    }
}
exports.RMQServiceBillers = RMQServiceBillers;
class RMQServiceBusiness {
    static getName() {
        return 'RMQ_SERVICE_BUSINESS';
    }
    static getQueueName() {
        return 'business_queue';
    }
}
exports.RMQServiceBusiness = RMQServiceBusiness;
class RMQServiceFiles {
    static getName() {
        return 'RMQ_SERVICE_FILES';
    }
    static getQueueName() {
        return 'files_queue';
    }
}
exports.RMQServiceFiles = RMQServiceFiles;
class RMQServiceGyms {
    static getName() {
        return 'RMQ_SERVICE_GYMS';
    }
    static getQueueName() {
        return 'gyms_queue';
    }
}
exports.RMQServiceGyms = RMQServiceGyms;
class RMQServiceNotifiers {
    static getName() {
        return 'RMQ_SERVICE_NOTIFIERS';
    }
    static getQueueName() {
        return 'notifiers_queue';
    }
}
exports.RMQServiceNotifiers = RMQServiceNotifiers;
class RMQServiceOrders {
    static getName() {
        return 'RMQ_SERVICE_ORDERS';
    }
    static getQueueName() {
        return 'orders_queue';
    }
}
exports.RMQServiceOrders = RMQServiceOrders;
class RMQServicePayments {
    static getName() {
        return 'RMQ_SERVICE_PAYMENTS';
    }
    static getQueueName() {
        return 'payments_queue';
    }
}
exports.RMQServicePayments = RMQServicePayments;
class RMQServiceStores {
    static getName() {
        return 'RMQ_SERVICE_STORES';
    }
    static getQueueName() {
        return 'stores_queue';
    }
}
exports.RMQServiceStores = RMQServiceStores;
class RMQServiceSubscriptions {
    static getName() {
        return 'RMQ_SERVICE_SUBSCRIPTIONS';
    }
    static getQueueName() {
        return 'subscriptions_queue';
    }
}
exports.RMQServiceSubscriptions = RMQServiceSubscriptions;
class RMQServiceUsers {
    static getName() {
        return 'RMQ_SERVICE_USERS';
    }
    static getQueueName() {
        return 'users_queue';
    }
}
exports.RMQServiceUsers = RMQServiceUsers;
//# sourceMappingURL=services.js.map