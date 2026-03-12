"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const core_1 = require("@nestjs/core");
const app_module_1 = require("./app.module");
const microservices_1 = require("@nestjs/microservices");
const common_1 = require("@nestjs/common");
const config_1 = require("./config");
async function bootstrap() {
    const logger = new common_1.Logger('Main');
    const app = await core_1.NestFactory.create(app_module_1.AppModule);
    app.enableShutdownHooks();
    app.connectMicroservice({
        transport: microservices_1.Transport.RMQ,
        options: {
            urls: config_1.envs.rmqServers,
            queue: config_1.RMQServiceBusiness.getQueueName(),
            queueOptions: { durable: true },
            socketOptions: {
                heartbeatIntervalInSeconds: 5,
                reconnectTimeInSeconds: 5,
            },
        },
    });
    app.useGlobalPipes(new common_1.ValidationPipe({
        whitelist: true,
        forbidNonWhitelisted: true,
    }));
    await app.startAllMicroservices();
    await app.listen(config_1.envs.port);
    logger.log(`Businesses Microservice running on port ${config_1.envs.port}`);
}
bootstrap();
//# sourceMappingURL=main.js.map